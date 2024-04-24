package api.profile.avatar;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import common.HttpStatusCode;
import common.dto.UserPasswordDto;
import common.service.UploadService;
import exceptions.CustomException;
import exceptions.ServerException;
import modules.auth.AuthRepository;
import modules.auth.service.AuthService;
import modules.profile.dto.UrlDto;
import modules.profile.service.ProfileService;
import stores.session.Session;
import stores.session.SessionKey;
import utils.RequestUtils;
import utils.ResponseUtils;

@WebServlet("/profile/avatar")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class ProfileAvatarServlet extends HttpServlet {
    final RequestUtils requestUtils = new RequestUtils();
    final ResponseUtils responseUtils = new ResponseUtils();
    final UploadService uploadService = new UploadService();
    final ProfileService profileService = new ProfileService();
    final AuthRepository authRepository = new AuthRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = requestUtils.getSession(req);
        UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
        int userId = userPasswordDto.getId();
        Part imagePart = requestUtils.getImagePartMultipartFormData(req);
        String imageName = UUID.randomUUID().toString() + imagePart.getSubmittedFileName();
        try {
            String url = uploadService.uploadImage(imagePart, imageName, userId);
            boolean result = profileService.updateAvatarUrl(userId, url);
            if (!result) {
                throw new Exception();
            }
            UserPasswordDto newUserPasswordDto = authRepository.getUserByEmail(userPasswordDto.getEmail());
            session.setAttribute(SessionKey.USER_PASSWORD_DTO, newUserPasswordDto);
            responseUtils.responseJson(resp, new UrlDto(url));
        } catch (Throwable throwable) {
            throw new ServerException();
        }
    }
}
