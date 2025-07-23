document.addEventListener('DOMContentLoaded', function () {
  const textarea = document.getElementById('intro');
  const charCount = document.getElementById('charCount');

  if (textarea && charCount) {
    const maxLength = textarea.getAttribute('maxlength');

    textarea.addEventListener('input', () => {
      const remaining = maxLength - textarea.value.length;
      charCount.textContent = `${remaining}자 남음`;
    });
  }
});
