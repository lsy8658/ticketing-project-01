import { ChangeEvent } from "react";
import styles from "./FormInput.module.css";

type FormInputProps = {
  type: string;
  name: string;
  placeholder: string;
  value: string;
  onChange: (e: ChangeEvent<HTMLInputElement>) => void;
};

export const FormInput = ({
  type,
  name,
  placeholder,
  value,
  onChange,
}: FormInputProps) => {
  return (
    <input
      type={type}
      name={name}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      required
      className={styles.input}
    />
  );
};
