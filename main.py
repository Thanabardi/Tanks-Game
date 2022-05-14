str = "6, 7, 8, 9, 16, 17"
s = "1"
l = []

for x in str.split(", "):
    l.append("{ " + s + ", " + x + " }")

print(", ".join(l))
