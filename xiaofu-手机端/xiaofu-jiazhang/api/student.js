import request from '@/utils/request'

function formatGradeClassText(grade = '', classValue = '') {
  const nextGrade = `${grade || ''}`.trim()
  const nextClass = `${classValue || ''}`.trim()

  if (!nextGrade) return nextClass
  if (!nextClass) return nextGrade
  if (nextClass.startsWith(nextGrade)) return nextClass
  return `${nextGrade}${nextClass}`
}

function normalizeStudent(item = {}) {
  const schoolId = item.schoolId || item.shcoolId || item.deptId || item.sysDepts?.deptId || ''
  const schoolName = item.school || item.schoolName || item.sysDepts?.deptName || ''
  const grade = item.grade || item.gradeName || ''
  const classValue = item.className || item.classId || item.class || ''

  return {
    ...item,
    id: item.id || '',
    studentId: item.studentId || item.id || '',
    studentNo: item.studentNo || item.student_no || item.code || '',
    studentName: item.name || item.studentName || '',
    name: item.name || item.studentName || '',
    schoolId,
    shcoolId: schoolId,
    deptId: schoolId,
    school: schoolName,
    schoolName,
    grade,
    gradeName: grade,
    classId: item.classId || item.class_id || classValue || '',
    className: classValue,
    gradeClassText: formatGradeClassText(grade, classValue),
    birthday: item.birthday || '',
    idNumber: item.idNumber || '',
    topSize: item.topSize || item.top_size || '',
    bottomSize: item.bottomSize || item.bottom_size || '',
    shoesSize: item.shoesSize || item.shoes_size || '',
    sex: item.sex || item.gender || '0',
    gender: item.sex || item.gender || '0'
  }
}

function buildStudentForm(data = {}) {
  const parts = []
  const append = (key, value) => {
    if (value !== undefined && value !== null && value !== '') {
      parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    }
  }

  append('id', data.id)
  append('name', data.name || data.studentName)
  append('sex', data.sex || (data.gender === '1' ? '1' : '0'))
  append('studentNo', data.studentNo || data.code)
  append('shcoolId', data.shcoolId || data.schoolId || data.deptId)
  append('schoolName', data.school || data.schoolName)
  append('grade', data.grade || data.gradeName)
  append('gradeName', data.grade || data.gradeName)
  append('classId', data.classId || data.className)
  append('className', data.className)
  append('birthday', data.birthday)
  append('idNumber', data.idNumber)
  append('topSize', data.topSize)
  append('bottomSize', data.bottomSize)
  append('shoesSize', data.shoesSize)
  append('simId', uni.getStorageSync('simId') || '')
  return parts.join('&')
}

export function listMyStudents(params = {}) {
  return request({
    url: '/school/schoolStudentInfo/list',
    method: 'GET',
    params
  }).then((res) => ({
    ...res,
    rows: (res.rows || []).map(normalizeStudent)
  }))
}

export function getDefaultStudent() {
  return listMyStudents({ pageNum: 1, pageSize: 10 }).then((res) => ({
    code: 200,
    data: (res.rows || [])[0] || null
  }))
}

export function getStudent(id) {
  return request({
    url: `/school/schoolStudentInfo/${id}`,
    method: 'GET'
  }).then((res) => ({
    ...res,
    data: normalizeStudent(res.data || res)
  }))
}

export function listSchoolOptions(name = '') {
  return request({
    url: '/system/open/link/getSchoolByName',
    method: 'GET',
    params: { name }
  }).then((res) => {
    const rows = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : []
    return rows.map((item) => ({
      name: item.deptName,
      value: Number(item.deptId)
    }))
  })
}

export function addStudent(data) {
  return request({
    url: '/school/schoolStudentInfo',
    method: 'POST',
    data: buildStudentForm(data),
    header: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function updateStudent(data) {
  return request({
    url: '/school/schoolStudentInfo',
    method: 'PUT',
    data: {
      id: data.id,
      name: data.name || data.studentName,
      sex: data.sex || (data.gender === '1' ? '1' : '0'),
      studentNo: data.studentNo || data.code,
      shcoolId: data.shcoolId || data.schoolId || data.deptId,
      schoolName: data.school || data.schoolName,
      grade: data.grade || data.gradeName,
      gradeName: data.grade || data.gradeName,
      classId: data.classId || data.className,
      className: data.className,
      birthday: data.birthday,
      idNumber: data.idNumber,
      topSize: data.topSize,
      bottomSize: data.bottomSize,
      shoesSize: data.shoesSize
    }
  })
}

export function deleteStudent(id) {
  return request({
    url: `/school/schoolStudentInfo/${id}`,
    method: 'DELETE'
  })
}
