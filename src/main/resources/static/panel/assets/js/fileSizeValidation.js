var uploadField = document.getElementById("attachment");

uploadField.onchange = function() {
  if (this.files[0].size > 512000) {
    alert("حجم فایل انتخابی بیشتر از سقف مجاز است.");
    this.value = "";
  }
};