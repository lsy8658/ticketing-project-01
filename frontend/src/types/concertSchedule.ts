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

export type ConcertScheduleCreateRequest = {
  concertId: number;
  venueId: number;
  startAt: string;
  endAt: string;
};
