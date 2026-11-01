const fs = require('fs');
const path = 'D:/校服项目/xiaofu-PC端/xiaofu-xx/src/store/modules/permission.js';
let c = fs.readFileSync(path, 'utf-8');

// Fix: restore the hasXuexiaoRole ternary + serverRoutes variable
c = c.replace(
  /(getRouters\(\)\.then\(\(res\) => \{\s*\n\s*)const normalizedRoutes = normalizeNonSchoolRoutes\(JSON\.parse\(JSON\.stringify\(res\.data \|\| \[\]\)\)\);/,
  '$1const serverRoutes = JSON.parse(JSON.stringify(res.data || []));\n          const normalizedRoutes = hasXuexiaoRole\n            ? promoteSchoolTonggouMenus(serverRoutes)\n            : normalizeNonSchoolRoutes(serverRoutes);'
);

fs.writeFileSync(path, c, 'utf-8');
console.log('Fixed');