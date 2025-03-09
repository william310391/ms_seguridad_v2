package com.ms_seguridad.repository;

public class Queries {
    public class USERS {
        public static final String GET_USERS_BY_USERNAME = """
                    select * from users WHERE USERNAME=:USERNAME;;
                """;
    }

    public class ROLE {
        public static final String GET_ROLE_BY_IDUSER = """
                    select r.id,r.role_name from user_roles ur
                    inner join roles r on r.id= ur.role_id
                    where user_id= :user_id;
                """;
    }

    public class PERMISSION {
        public static final String GET_PERMISSION_BY_IDROLE = """
                    select * from role_permissions rp
                    inner join permissions p on p.id =rp.permission_id
                    where rp.role_id in (:role_id);
                """;
    }

}
