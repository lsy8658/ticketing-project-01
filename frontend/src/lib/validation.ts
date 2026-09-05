export const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
export const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d).{8,}$/;

export const isValidEmail = (email: string) => emailRegex.test(email);
export const isValidPassword = (password: string) =>
  passwordRegex.test(password);
