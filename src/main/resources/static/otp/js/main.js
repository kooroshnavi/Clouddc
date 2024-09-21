let timerOn = true;
function timer(remaining) {
  var h = Math.floor(remaining / 3600);
  var m = Math.floor(remaining % 3600 / 60);
  var s = Math.floor(remaining % 3600 % 60);
  m = m < 10 ? "0" + m : m;
  s = s < 10 ? "0" + s : s;
  h = h < 10 ? "0" + h : h;
  document.getElementById("countdown").innerHTML = ` آخرین کد ارسالی تا  ${s} : ${m} : ${h} معتبر است`;
  remaining -= 1;
  if (remaining >= 0 && timerOn) {
    setTimeout(function () {
      timer(remaining);
    }, 1000);
    document.getElementById("resend").innerHTML = `
    `;
    return;
  }
  if (!timerOn) {
    return;
  }
  document.getElementById("resend").innerHTML = `Don't receive the code?
  <span class="font-weight-bold text-color cursor" onclick="timer(60)">Resend</span>`;
}
timer(secondsLeft);
