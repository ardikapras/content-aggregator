export interface ParserConfigDto {
  id: string;
  name: string;
  description: string;
  authorSelectors: string[];
  contentSelectors: string[];
  nextPageSelector?: string;
  contentFilters: string[];
  createdAt: string;
  updatedAt: string;
}

export interface ParserFormData {
  name: string;
  description: string;
  authorSelectors: string;
  contentSelectors: string;
  nextPageSelector: string;
  contentFilters: string;
}

export interface ParserTestResponse {
  author?: string;
  contentPreview?: string;
  success: boolean;
  message?: string;
}
