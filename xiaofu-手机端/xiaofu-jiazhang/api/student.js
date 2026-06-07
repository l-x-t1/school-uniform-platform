import request from '@/utils/request'

function normalizeStudent(item = {}) {
  return {
    ...item,
    id: item.id || item.studentId,
    studentId: item.id || item.studentId,
    studentName: item.name || item.studentName || '',
    name: item.name || item.studentName || '',
    schoolId: item.schoolId || item.shcoolId || item.deptId || item.sysDepts?.deptId || '',
    shcoolId: item.shcoolId || item.schoolId || item.deptId || item.sysDepts?.deptId || '',
    deptId: item.deptId || item.schoolId || item.shcoolId || item.sysDepts?.deptId || '',
    school: item.school || item.schoolName || item.sysDepts?.deptName || '',
    schoolName: item.school || item.schoolName || item.sysDepts?.deptName || '',
    grade: item.grade || item.gradeName || '',
    gradeName: item.grade || item.gradeName || '',
    classId: item.classId || item.className || '',
    className: item.className || item.class || '',
    birthday: item.birthday || '',
    idNumber: item.idNumber || '',
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
  append('studentId', data.studentId || data.code)
  append('shcoolId', data.shcoolId || data.schoolId || data.deptId)
  append('schoolName', data.school || data.schoolName)
  append('grade', data.grade || data.gradeName)
  append('gradeName', data.grade || data.gradeName)
  append('classId', data.classId || data.className)
  append('className', data.className)
  append('birthday', data.birthday)
  append('idNumber', data.idNumber)
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
      studentId: data.studentId || data.code,
      shcoolId: data.shcoolId || data.schoolId || data.deptId,
      schoolName: data.school || data.schoolName,
      grade: data.grade || data.gradeName,
      gradeName: data.grade || data.gradeName,
      classId: data.classId || data.className,
      className: data.className,
      birthday: data.birthday,
      idNumber: data.idNumber
    }
  })
}

export function deleteStudent(id) {
  return request({
    url: `/school/schoolStudentInfo/${id}`,
    method: 'DELETE'
  })
}
