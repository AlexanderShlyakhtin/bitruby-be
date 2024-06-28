package kg.bitruby.bybitintegrator.common;

import kg.bitruby.commonmodule.exceptions.BitrubyRuntimeExpection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;


@Slf4j
public final class AppContextHolder {

    private AppContextHolder() {}
    private static final ThreadLocal<RequestContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<UserContext> contextUserThreadLocal = new ThreadLocal<>();
    public static void setRqUid(UUID rqUid) {
        contextThreadLocal.set(new RequestContext(rqUid));
    }
    public static void setUserId(UUID userId) {
        contextUserThreadLocal.set(new UserContext(userId));
    }
    public static UUID getContextRequestId() {
        return Optional.ofNullable(contextThreadLocal.get())
                .map(RequestContext::getRequestId)
                .orElseThrow(() -> {
                    log.error("Ошибка обработки запроса, в контексте запроса отсутствует Request Id");
                    return new BitrubyRuntimeExpection("В контексте запроса отсутствует rqUid");
                });
    }

    public static UUID getContextUserId() {
        return Optional.ofNullable(contextUserThreadLocal.get())
            .map(UserContext::getUserId)
            .orElse(null);
    }

    public static void cleanContext() {
        contextThreadLocal.remove();
        contextUserThreadLocal.remove();;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class RequestContext {
        private final UUID requestId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class UserContext {
        private final UUID userId;
    }

}
