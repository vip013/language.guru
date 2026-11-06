package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

data class HandwritingValidationResult(
    val isCorrect: Boolean,
    val score: Int,
    val reason: String = ""
)

object HandwritingRecognitionHelper {

    fun getLanguageName(langCode: String): String = when (langCode.lowercase()) {
        "te" -> "Telugu"
        "ta" -> "Tamil"
        "hi" -> "Hindi"
        "ar" -> "Arabic"
        "kn" -> "Kannada"
        "ml" -> "Malayalam"
        "bn" -> "Bengali"
        "mr" -> "Marathi"
        "gu" -> "Gujarati"
        else -> "English"
    }

    /**
     * Checks if the drawing is completely empty, contains too few points,
     * or is merely an accidental touch/dot.
     */
    fun isDrawingEmptyOrAccidental(strokes: List<DrawingStroke>): Boolean {
        if (strokes.isEmpty()) return true

        val allPoints = strokes.flatMap { it.points }
        if (allPoints.size < 3) return true

        var totalLength = 0f
        for (stroke in strokes) {
            val pts = stroke.points
            for (i in 1 until pts.size) {
                val dx = pts[i].x - pts[i - 1].x
                val dy = pts[i].y - pts[i - 1].y
                totalLength += hypot(dx, dy)
            }
        }
        if (totalLength < 35f) return true

        val minX = allPoints.minOf { it.x }
        val maxX = allPoints.maxOf { it.x }
        val minY = allPoints.minOf { it.y }
        val maxY = allPoints.maxOf { it.y }
        val width = maxX - minX
        val height = maxY - minY

        // Less than 18x18 px total area is an accidental click/tap
        return width < 18f && height < 18f
    }

    /**
     * Determines whether a target character is structurally complex
     * (e.g. contains combining vowel signs/diacritics or multiple codepoints)
     * using universal Unicode classification rather than hardcoded character checks.
     */
    fun isComplexStructuralTarget(targetText: String): Boolean {
        if (targetText.length > 1) return true
        return targetText.any {
            val charType = Character.getType(it)
            charType == Character.NON_SPACING_MARK.toInt() ||
            charType == Character.COMBINING_SPACING_MARK.toInt() ||
            charType == Character.ENCLOSING_MARK.toInt()
        }
    }

    /**
     * Normalizes user drawing strokes into a centered box with standardized bounds and padding.
     * This eliminates size and position discrepancies while preserving the character's aspect ratio.
     */
    fun normalizeStrokes(
        strokes: List<DrawingStroke>,
        targetWidth: Float,
        targetHeight: Float,
        padding: Float = 36f
    ): List<DrawingStroke> {
        val allPoints = strokes.flatMap { it.points }
        if (allPoints.isEmpty()) return emptyList()

        val minX = allPoints.minOf { it.x }
        val maxX = allPoints.maxOf { it.x }
        val minY = allPoints.minOf { it.y }
        val maxY = allPoints.maxOf { it.y }

        val boxW = max(maxX - minX, 1f)
        val boxH = max(maxY - minY, 1f)
        val maxDim = max(boxW, boxH)

        val availableW = targetWidth - (2 * padding)
        val availableH = targetHeight - (2 * padding)
        val availableDim = min(availableW, availableH)
        val scale = availableDim / maxDim

        val centerX = (minX + maxX) / 2f
        val centerY = (minY + maxY) / 2f

        val destCenterX = targetWidth / 2f
        val destCenterY = targetHeight / 2f

        return strokes.map { stroke ->
            val normPoints = stroke.points.map { p ->
                val nx = (p.x - centerX) * scale + destCenterX
                val ny = (p.y - centerY) * scale + destCenterY
                DrawingPoint(nx, ny)
            }
            stroke.copy(points = normPoints, width = 20f)
        }
    }

    /**
     * Creates a high-clarity dual panel comparison bitmap:
     * - Left Panel: Clearly rendered Target Reference character
     * - Right Panel: Normalized, centered Learner's handwriting
     * This allows AI or visual evaluators to assess character identity without requiring pixel-overlay alignment.
     */
    fun createNormalizedComparisonBitmap(
        targetText: String,
        strokes: List<DrawingStroke>,
        langName: String
    ): Bitmap {
        val panelW = 380
        val panelH = 380
        val totalW = panelW * 2
        val totalH = panelH

        val bitmap = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Slate chalkboard background
        val bgPaint = Paint().apply {
            color = Color.parseColor("#1B2631")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, totalW.toFloat(), totalH.toFloat(), bgPaint)

        // Divider between panels
        val dividerPaint = Paint().apply {
            color = Color.parseColor("#37474F")
            strokeWidth = 3f
        }
        canvas.drawLine(panelW.toFloat(), 0f, panelW.toFloat(), totalH.toFloat(), dividerPaint)

        // Subtitle text paint
        val labelPaint = Paint().apply {
            color = Color.parseColor("#90A4AE")
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Left Panel Header
        canvas.drawText("TARGET ($langName)", panelW / 2f, 36f, labelPaint)

        // Render target text centered in left panel
        val targetPaint = Paint().apply {
            color = Color.parseColor("#FFF9C4") // Soft chalk yellow
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        val targetRatio = when (targetText.length) {
            1 -> 0.55f
            2 -> 0.40f
            3 -> 0.30f
            else -> 0.22f
        }
        targetPaint.textSize = (panelW * targetRatio).coerceIn(70f, 220f)
        val tBounds = Rect()
        targetPaint.getTextBounds(targetText, 0, targetText.length, tBounds)
        val tY = (panelH / 2f) + (tBounds.height() / 2f) - tBounds.bottom + 12f
        canvas.drawText(targetText, panelW / 2f, tY, targetPaint)

        // Right Panel Header
        canvas.drawText("LEARNER'S WRITING", panelW + (panelW / 2f), 36f, labelPaint)

        // Normalize strokes in right panel
        val normalizedRightStrokes = normalizeStrokes(
            strokes = strokes,
            targetWidth = panelW.toFloat(),
            targetHeight = panelH.toFloat(),
            padding = 48f
        )

        // Render normalized learner strokes in right panel
        val strokePaint = Paint().apply {
            color = Color.parseColor("#4DD0E1") // Vibrant chalk cyan
            strokeWidth = 22f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }

        normalizedRightStrokes.forEach { stroke ->
            if (stroke.points.size > 1) {
                val path = Path().apply {
                    val first = stroke.points.first()
                    moveTo(first.x + panelW, first.y + 12f)
                    for (i in 1 until stroke.points.size) {
                        val p = stroke.points[i]
                        lineTo(p.x + panelW, p.y + 12f)
                    }
                }
                canvas.drawPath(path, strokePaint)
            } else if (stroke.points.size == 1) {
                val p = stroke.points[0]
                canvas.drawCircle(p.x + panelW, p.y + 12f, 11f, strokePaint)
            }
        }

        return bitmap
    }

    /**
     * Local structural shape & feature similarity evaluator.
     * Compares normalized density distribution, Chamfer distance, target coverage,
     * and aspect ratio. Tolerant of natural handwriting variations while reliably
     * rejecting blank/accidental marks and distinctly different characters.
     */
    fun evaluateStructuralSimilarity(
        targetText: String,
        strokes: List<DrawingStroke>
    ): HandwritingValidationResult {
        if (isDrawingEmptyOrAccidental(strokes)) {
            return HandwritingValidationResult(
                isCorrect = false,
                score = 0,
                reason = "Drawing is empty or too small"
            )
        }

        val gridSize = 100
        val targetBmp = Bitmap.createBitmap(gridSize, gridSize, Bitmap.Config.ARGB_8888)
        val userBmp = Bitmap.createBitmap(gridSize, gridSize, Bitmap.Config.ARGB_8888)

        val tCanvas = Canvas(targetBmp)
        val uCanvas = Canvas(userBmp)

        // Target rendering on normalized 100x100
        val tPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        val tRatio = when (targetText.length) {
            1 -> 0.72f
            2 -> 0.52f
            3 -> 0.38f
            else -> 0.28f
        }
        tPaint.textSize = (gridSize * tRatio).coerceIn(24f, 76f)
        val tBounds = Rect()
        tPaint.getTextBounds(targetText, 0, targetText.length, tBounds)
        val tX = gridSize / 2f
        val tY = (gridSize / 2f) + (tBounds.height() / 2f) - tBounds.bottom
        tCanvas.drawText(targetText, tX, tY, tPaint)

        // User normalized strokes on 100x100
        val normStrokes = normalizeStrokes(strokes, gridSize.toFloat(), gridSize.toFloat(), padding = 12f)
        val uPaint = Paint().apply {
            color = Color.WHITE
            strokeWidth = 9f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }
        normStrokes.forEach { stroke ->
            if (stroke.points.size > 1) {
                val path = Path().apply {
                    val first = stroke.points.first()
                    moveTo(first.x, first.y)
                    for (i in 1 until stroke.points.size) {
                        lineTo(stroke.points[i].x, stroke.points[i].y)
                    }
                }
                uCanvas.drawPath(path, uPaint)
            } else if (stroke.points.size == 1) {
                val p = stroke.points[0]
                uCanvas.drawCircle(p.x, p.y, 4.5f, uPaint)
            }
        }

        // Collect ink pixel points
        val targetPixels = mutableListOf<Pair<Int, Int>>()
        val userPixels = mutableListOf<Pair<Int, Int>>()

        val tPixelsArray = IntArray(gridSize * gridSize)
        val uPixelsArray = IntArray(gridSize * gridSize)
        targetBmp.getPixels(tPixelsArray, 0, gridSize, 0, 0, gridSize, gridSize)
        userBmp.getPixels(uPixelsArray, 0, gridSize, 0, 0, gridSize, gridSize)

        for (y in 0 until gridSize) {
            for (x in 0 until gridSize) {
                val idx = y * gridSize + x
                if ((tPixelsArray[idx] and 0xFF) > 40) {
                    targetPixels.add(x to y)
                }
                if ((uPixelsArray[idx] and 0xFF) > 40) {
                    userPixels.add(x to y)
                }
            }
        }

        targetBmp.recycle()
        userBmp.recycle()

        if (userPixels.isEmpty() || targetPixels.isEmpty()) {
            return HandwritingValidationResult(isCorrect = false, score = 0, reason = "No ink captured")
        }

        // 1. Grid block density cosine similarity (8x8 = 64 zones)
        val blocks = 8
        val blockSize = gridSize / blocks
        val tZone = FloatArray(blocks * blocks)
        val uZone = FloatArray(blocks * blocks)

        targetPixels.forEach { (x, y) ->
            val bx = (x / blockSize).coerceIn(0, blocks - 1)
            val by = (y / blockSize).coerceIn(0, blocks - 1)
            tZone[by * blocks + bx] += 1f
        }
        userPixels.forEach { (x, y) ->
            val bx = (x / blockSize).coerceIn(0, blocks - 1)
            val by = (y / blockSize).coerceIn(0, blocks - 1)
            uZone[by * blocks + bx] += 1f
        }

        var dotProduct = 0f
        var tNormSq = 0f
        var uNormSq = 0f
        for (i in 0 until blocks * blocks) {
            dotProduct += tZone[i] * uZone[i]
            tNormSq += tZone[i] * tZone[i]
            uNormSq += uZone[i] * uZone[i]
        }
        val cosineSim = if (tNormSq > 0f && uNormSq > 0f) {
            (dotProduct / (kotlin.math.sqrt(tNormSq) * kotlin.math.sqrt(uNormSq))).coerceIn(0f, 1f)
        } else 0f

        // 2. Chamfer Distance (sample points for fast local calculation)
        val userSample = if (userPixels.size > 80) {
            val step = userPixels.size / 80
            userPixels.filterIndexed { index, _ -> index % step == 0 }
        } else userPixels

        var totalMinDist = 0f
        userSample.forEach { (ux, uy) ->
            var minDistSq = Float.MAX_VALUE
            for ((tx, ty) in targetPixels) {
                val d2 = ((ux - tx) * (ux - tx) + (uy - ty) * (uy - ty)).toFloat()
                if (d2 < minDistSq) {
                    minDistSq = d2
                    if (d2 <= 4f) break
                }
            }
            totalMinDist += kotlin.math.sqrt(minDistSq)
        }
        val avgUserDist = totalMinDist / userSample.size
        // 24px in 100x100 is ~24% tolerance radius
        val proximityScore = (1f - (avgUserDist / 26f)).coerceIn(0f, 1f)

        // 3. Target region coverage
        var coveredTargetZones = 0
        var totalActiveTargetZones = 0
        for (i in 0 until blocks * blocks) {
            if (tZone[i] > 0) {
                totalActiveTargetZones++
                // check self or neighbor active in userZone
                val bx = i % blocks
                val by = i / blocks
                var matched = false
                for (dy in -1..1) {
                    for (dx in -1..1) {
                        val nx = bx + dx
                        val ny = by + dy
                        if (nx in 0 until blocks && ny in 0 until blocks) {
                            if (uZone[ny * blocks + nx] > 0) {
                                matched = true
                                break
                            }
                        }
                    }
                    if (matched) break
                }
                if (matched) coveredTargetZones++
            }
        }
        val coverageScore = if (totalActiveTargetZones > 0) {
            (coveredTargetZones.toFloat() / totalActiveTargetZones).coerceIn(0f, 1f)
        } else 0f

        // Combined structural score (0 to 100)
        var rawScore = (cosineSim * 45f + proximityScore * 35f + coverageScore * 20f)

        // Adaptive structural leniency boost for complex glyphs (e.g., multi-character ligatures,
        // characters with combining diacritics/dots, or high zone density)
        val hasCombiningMarks = targetText.any {
            val charType = Character.getType(it)
            charType == Character.NON_SPACING_MARK.toInt() ||
            charType == Character.COMBINING_SPACING_MARK.toInt() ||
            charType == Character.ENCLOSING_MARK.toInt()
        }
        val isMultiChar = targetText.length > 1
        val isIntricate = totalActiveTargetZones > 16

        if (hasCombiningMarks || isMultiChar || isIntricate) {
            val densityRatio = (totalActiveTargetZones.toFloat() / 25f).coerceIn(0.8f, 1.3f)
            rawScore += 10f * densityRatio
        }

        val finalScore = rawScore.toInt().coerceIn(0, 100)

        // Tolerant threshold: >= 48 allows natural variations while reliably catching wrong/blank inputs
        val isMatch = finalScore >= 48

        return HandwritingValidationResult(
            isCorrect = isMatch,
            score = finalScore,
            reason = "cosine: ${(cosineSim*100).toInt()}%, prox: ${(proximityScore*100).toInt()}%, cov: ${(coverageScore*100).toInt()}%"
        )
    }
}
