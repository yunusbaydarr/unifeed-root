import axios from 'axios';
import type { ApiProblem, ValidationError } from './types';

const isRecord = (value: unknown): value is Record<string, unknown> =>
  typeof value === 'object' && value !== null;

export const isApiProblem = (value: unknown): value is ApiProblem =>
  isRecord(value) && typeof value.status === 'number' && typeof value.title === 'string' && typeof value.detail === 'string';

export const parseApiProblem = (error: unknown): ApiProblem => {
  if (axios.isAxiosError(error) && isApiProblem(error.response?.data)) return error.response.data;
  if (isApiProblem(error)) return error;
  return {
    type: 'about:blank',
    title: 'Unexpected error',
    status: axios.isAxiosError(error) ? (error.response?.status ?? 0) : 0,
    detail: error instanceof Error ? error.message : 'An unexpected error occurred.',
  };
};

export const validationErrorsFor = (problem: ApiProblem, field: string): ValidationError[] =>
  problem.validationErrors?.filter((error) => error.field === field) ?? [];
