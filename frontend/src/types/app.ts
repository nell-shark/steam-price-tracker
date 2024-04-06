export type AppsByPage = {
  totalElements: number;
  totalPages: number;
  size: number;
  content: [
    {
      id: number;
      name: string;
      imageUrl: string;
    }
  ];
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  last: boolean;
  numberOfElements: number;
  pageable: {
    offset: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    pageNumber: number;
    pageSize: number;
    unpaged: boolean;
    paged: boolean;
  };
  empty: boolean;
};
