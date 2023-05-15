const countDown = (n) => num2(Math.floor(n / 60)) + ':' + num2(n % 60);

const num2 = (n) => (n <= 9 ? '0' : '') + n;

export {
    countDown
}