import re

file_paths = [
    "app/src/main/java/com/example/data/LanguageData.kt",
    "app/src/main/java/com/example/data/TranslationHelper.kt"
]

SCRIPTS = {
    "Devanagari": (0x0900, 0x097F),
    "Bengali": (0x0980, 0x09FF),
    "Gujarati": (0x0A80, 0x0AFF),
    "Tamil": (0x0B80, 0x0BFF),
    "Telugu": (0x0C00, 0x0C7F),
    "Kannada": (0x0C80, 0x0CFF),
    "Malayalam": (0x0D00, 0x0D7F),
    "Arabic": (0x0600, 0x06FF),
}

def get_scripts_of_string(s):
    detected = set()
    for char in s:
        cp = ord(char)
        for script_name, (start, end) in SCRIPTS.items():
            if start <= cp <= end:
                detected.add(script_name)
    return detected

for file_path in file_paths:
    print(f"\n==================== {file_path} ====================")
    with open(file_path, "r", encoding="utf-8") as f:
        lines = f.readlines()
    
    for idx, line in enumerate(lines):
        strings = re.findall(r'"((?:[^"\\]|\\.)*)"', line)
        for s in strings:
            detected_scripts = get_scripts_of_string(s)
            if len(detected_scripts) > 1:
                print(f"Line {idx + 1}: {line.strip()}")
                print(f"  String literal: {repr(s)}")
                print(f"  Scripts found: {list(detected_scripts)}")
                # Show unicode hex for non-ascii characters
                unicode_hex = []
                for c in s:
                    if ord(c) > 127:
                        unicode_hex.append(f"{c}(U+{ord(c):04X})")
                print(f"  Non-ASCII chars: {' '.join(unicode_hex)}")
