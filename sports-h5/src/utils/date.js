import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

// 设置语言为中文
dayjs.locale('zh-cn')

// 格式化日期
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  return dayjs(date).format(format)
}

// 格式化日期（仅日期部分）
export function formatDateOnly(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  return dayjs(date).format(format)
}

// 格式化时间（仅时间部分）
export function formatTime(date, format = 'HH:mm:ss') {
  if (!date) return ''
  return dayjs(date).format(format)
}

// 解析日期字符串为Date对象
export function parseDate(dateStr) {
  if (!dateStr) return null
  return dayjs(dateStr).toDate()
}

// 获取相对时间
export function getRelativeTime(date) {
  if (!date) return ''
  const now = dayjs()
  const target = dayjs(date)
  const diff = now.diff(target, 'minute')

  if (diff < 1) return '刚刚'
  if (diff < 60) return `${diff}分钟前`
  
  const hourDiff = now.diff(target, 'hour')
  if (hourDiff < 24) return `${hourDiff}小时前`
  
  const dayDiff = now.diff(target, 'day')
  if (dayDiff < 30) return `${dayDiff}天前`
  
  const monthDiff = now.diff(target, 'month')
  if (monthDiff < 12) return `${monthDiff}个月前`
  
  return `${now.diff(target, 'year')}年前`
}

// 检查日期是否过期
export function isExpired(date) {
  if (!date) return false
  return dayjs().isAfter(dayjs(date))
}

// 获取两个日期之间的天数
export function getDaysBetween(startDate, endDate) {
  if (!startDate || !endDate) return 0
  return dayjs(endDate).diff(dayjs(startDate), 'day')
}

// 获取日期范围
export function getDateRange(startDate, endDate, format) {
  if (!startDate || !endDate) return ''
  
  // 如果提供了format参数，返回日期数组
  if (format) {
    const dates = []
    let currentDate = dayjs(startDate)
    const lastDate = dayjs(endDate)

    while (currentDate.isBefore(lastDate) || currentDate.isSame(lastDate, 'day')) {
      dates.push(currentDate.format(format))
      currentDate = currentDate.add(1, 'day')
    }

    return dates
  }
  
  // 否则返回格式化的日期范围字符串
  return `${formatDate(startDate, 'MM-DD HH:mm')} 至 ${formatDate(endDate, 'MM-DD HH:mm')}`
}

// 判断日期是否在范围内
export function isDateInRange(date, startDate, endDate) {
  if (!date || !startDate || !endDate) return false
  const current = dayjs(date)
  return current.isAfter(startDate) && current.isBefore(endDate)
}

// 获取当前时间
export function getCurrentDate() {
  return dayjs().format('YYYY-MM-DD HH:mm:ss')
}

// 日期比较
export function compareDates(date1, date2) {
  if (!date1 || !date2) return 0
  return dayjs(date1).diff(dayjs(date2))
} 