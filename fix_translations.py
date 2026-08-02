import re

file_path = "app/src/main/java/com/example/data/TranslationHelper.kt"

with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Precise replacements
replacements = {
    # 1. Payasam/Kheer Malayalam
    '"ml" to "பாயசம்"': '"ml" to "പായസം"',
    
    # 2. Fruits/Flowers Mango Malayalam
    '"ml" to "மாம்பழம்"': '"ml" to "മാമ്പഴം"',
    
    # 3. Fruits/Flowers Apple Malayalam
    '"ml" to "ஆப்பிள்"': '"ml" to "ആപ്പിൾ"',
    
    # 4. Fruits/Flowers Coconut Hindi (Thai char)
    '"hi" to "नารियल"': '"hi" to "नारियल"',
    
    # 5. Fruits/Flowers Sunflower Bengali (Tamil char)
    '"bn" to "சூর্যমুখী"': '"bn" to "সূর্যমুখী"',
    
    # 6. Fruits/Flowers Chrysanthemum Malayalam
    '"ml" to "ശെവന്തി"': '"ml" to "ശേവന്തി"',
    
    # 7. National Symbol Tiger Bengali (Devanagari char)
    '"bn" to "বাघ"': '"bn" to "বাঘ"',
    
    # 8. National Symbol Elephant Malayalam (Kannada char)
    '"ml" to "ಆನ"': '"ml" to "ആന"',
    
    # 9. State Chhattisgarh Arabic (Devanagari char)
    '"ar" to "تشاتيسघار"': '"ar" to "تشاتيسغار"',
    
    # 10. State Uttarakhand Arabic (Devanagari char)
    '"ar" to "أوتार آخاند"': '"ar" to "أوتاراخاند"',
    '"ar" to "أوتار آخاند"': '"ar" to "أوتاراخاند"',
    
    # 11. Capital Gandhinagar Arabic (Tamil char)
    '"ar" to "غانديناغार"': '"ar" to "غانديناغار"',
    
    # 12. Capital Chandigarh Arabic (Tamil/Devanagari char)
    '"ar" to "شانديغार"': '"ar" to "شانديغار"',
    
    # 13. Capital Shimla Bengali (Devanagari char)
    '"bn" to "शिमला"': '"bn" to "শিমলা"',
    
    # 14. Capital Bhopal Arabic (Devanagari char) and Gujarati (Devanagari char)
    '"ar" to "भोपाल"': '"ar" to "بوهपाल"',
    '"gu" to "ભોપાલ"': '"gu" to "ભોપાલ"',
    '"gu" to "ભોપal"': '"gu" to "ભોપાલ"',
    
    # 15. Capital Kohima Bengali (Tamil char)
    '"bn" to "கோஹிமா"': '"bn" to "কোহিমা"',
    
    # 16. Capital Gangtok Arabic (Katakana/English char)
    '"ar" to "ghanクトーク"': '"ar" to "غانغتوك"',
}

# Apply replacements
for old, new in replacements.items():
    if old in content:
        content = content.replace(old, new)
        print(f"Successfully replaced: {old} -> {new}")
    else:
        # Try a more relaxed search without spaces/quotes if needed
        print(f"Not found directly: {old}")

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Done fixing translations.")
