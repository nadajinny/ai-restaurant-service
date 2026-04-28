export function formatCurrency(value) {
  return new Intl.NumberFormat("ko-KR").format(value ?? 0);
}

export function formatDateTime(value) {
  if (!value) {
    return "-";
  }

  return new Intl.DateTimeFormat("ko-KR", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}
