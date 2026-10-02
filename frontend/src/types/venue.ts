export type VenueCreateRequest = {
  name: string;
  address: string;
  capacity: number;
  managerPhone: string;
};

export type VenueResponse = {
  id: number;
  name: string;
  address: string;
  capacity: number;
  managerPhone: string;
  hasSeats: boolean;
};

export type VenueUpdateRequest = {
  name: string;
  address: string;
  capacity: number;
  managerPhone: string;
};
