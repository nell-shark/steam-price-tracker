import { axiosInstance } from "@/services/axiosInstance";
import { User } from "@/types/user";

class UserService {
  public async postUser(user: User, captcha: string) {
    const { data } = await axiosInstance.post<number>(`/api/v1/users`, user, {
      params: {
        captcha
      }
    });
    return data;
  }

  public async login(user: User) {
    const params = new URLSearchParams();
    params.append("email", user.email);
    params.append("password", user.password);

    const { data } = await axiosInstance.post<number>(`/login`, params, {
      headers: {
        "Content-type": "application/x-www-form-urlencoded"
      }
    });
    return data;
  }
}

export const userService = new UserService();
