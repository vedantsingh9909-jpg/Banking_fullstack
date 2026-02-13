import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import userService from "../api/UserService";
import type { AuthInfo } from "../types/auth";

const useAuth = (updateAuthInfo: React.Dispatch<React.SetStateAction<AuthInfo | undefined>>) => {
  const [isLoadingAuth, setLoadingAuth] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        const result = await userService.me();
        updateAuthInfo({ username: result.user.username });
      } catch (error) {
        updateAuthInfo(undefined);
        navigate("/login");
      } finally {
        setLoadingAuth(false);
      }
    })();
  }, []);
  return {
    isLoadingAuth
  };
};

export default useAuth;
("CAP");
("Elastic search");
("Kafka", "Rabbitmq", "Event Sourching", "Bankacılık", "Kubernates", "Load Balancer");
