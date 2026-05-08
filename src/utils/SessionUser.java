package utils;

import dto.TaiKhoanDTO;

public class SessionUser {
    private static TaiKhoanDTO currentUser = null;

    public static void setCurrentUser(TaiKhoanDTO user) {
        currentUser = user;
    }

    public static TaiKhoanDTO getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}