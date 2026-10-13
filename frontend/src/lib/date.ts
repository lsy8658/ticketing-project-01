const HAS_OFFSET = /(Z|[+-]\d{2}:\d{2})$/;

export const parseKst = (value: string): Date =>
  new Date(HAS_OFFSET.test(value) ? value : `${value}+09:00`);
