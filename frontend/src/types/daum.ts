interface DaumPostcodeData {
  address: string;
}

interface Window {
  daum: {
    Postcode: new (options: {
      oncomplete: (data: DaumPostcodeData) => void;
    }) => { open: () => void };
  };
}
