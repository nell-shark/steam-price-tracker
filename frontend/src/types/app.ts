export type AppInfo = {
  id: number;
  name: string;
  imageUrl: string;
};

export type AppsByPage = {
  totalElements: number;
  totalPages: number;
  size: number;
  content: AppInfo[];
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

export type AppById = {
  id: number;
  name: string;
  type: string;
  free: boolean;
  headerImage: string;
  platforms: string[];
  shortDescription?: string;
  developers?: string;
  publishers?: string;
  website?: string;
  metacritic?: {
    score: number;
    url: string;
  };
  releaseDate?: {
    comingSoon: boolean;
    releaseDate: string;
  };
  prices: [
    {
      id: number;
      createdTime: string;
      currencyPriceMap: {
        USD?: number;
        KZT?: number;
        RUB?: number;
        EUR?: number;
      };
    }
  ];
};
