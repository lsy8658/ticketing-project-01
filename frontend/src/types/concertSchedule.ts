export type ConcertSchedule = {
  id: number;
  concert: {
    id: number;
    title: string;
  };
  venue: {
    id: number;
    name: string;
    address: string;
  };
  startAt: string;
};
