import { axiosInstance } from "@/services/axiosInstance";
import { User } from "@/types/user";

class UserService {
  public async postUser(user: User, captcha: string) {
    console.log(`cap ${captcha}`);
    const { data } = await axiosInstance.post<number>(`/api/v1/users`, user, {
      params: {
        captcha
      }
    });
    return data;
  }
}

export const userService = new UserService();
