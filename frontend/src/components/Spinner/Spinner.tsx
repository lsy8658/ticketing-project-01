import styles from "./Spinner.module.css";

export const Spinner = ({
  positionCenter = false,
}: {
  positionCenter?: boolean;
}) => {
  return (
    <div className={positionCenter ? styles.spinnerWrap : ""}>
      <div className={styles.spinner} />
    </div>
  );
};
