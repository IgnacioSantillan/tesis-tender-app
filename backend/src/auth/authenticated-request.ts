import { AuthenticatedUser } from './authenticated-user';

export interface AuthenticatedRequest {
  headers: {
    authorization?: string | string[];
  };
  user?: AuthenticatedUser;
}
