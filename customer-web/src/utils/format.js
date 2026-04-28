export function formatCurrency(value) {
  return new Intl.NumberFormat("ko-KR").format(value ?? 0);
}
