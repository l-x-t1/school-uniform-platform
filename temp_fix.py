import re
with open(r'D:\校服项目\xiaofu-PC端\xiaofu-xx\src\store\modules\permission.js', 'r', encoding='utf-8') as f:
    content = f.read()
pat = r'\nfunction ensureVoteResultMenu\(routes = \[\]\) \{[\s\S]*?\n\}\n'
m = re.search(pat, content)
print('Match found:', bool(m))
