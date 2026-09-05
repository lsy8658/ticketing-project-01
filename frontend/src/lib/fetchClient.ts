export const fetchClient = async <T>(
  path: string,
  options?: RequestInit,
): Promise<T | null> => {
  const res = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/${path}`,
    options,
  );

  if (!res.ok) return null;

  const data = res.json();
  return data;
};
