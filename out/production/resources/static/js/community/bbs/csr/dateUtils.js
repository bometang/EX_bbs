export const toDate = ldtStr =>
  new Date(ldtStr.includes('T') ? ldtStr : ldtStr.replace(' ', 'T'));

// “방금 전” 포맷터
export const formatRelativeTime = ldtStr => {
  const now  = Date.now();
  const past = toDate(ldtStr).getTime();
  const diff = now - past;          // ms

  const sec  = Math.floor(diff / 1000);
  if (sec < 60)  return `${sec}초 전`;

  const min  = Math.floor(sec / 60);
  if (min < 60)  return `${min}분 전`;

  const hour = Math.floor(min / 60);
  if (hour < 24) return `${hour}시간 전`;

  const day  = Math.floor(hour / 24);
  if (day < 7)   return `${day}일 전`;

  const d  = new Date(past);
  const yy = String(d.getFullYear() % 100).padStart(2, '0');
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${yy}.${mm}.${dd}`;
};