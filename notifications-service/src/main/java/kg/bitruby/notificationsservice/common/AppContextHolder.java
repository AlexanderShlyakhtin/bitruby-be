package kg.bitruby.notificationsservice.common;

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
    public static void setContext(UUID rqUid) {
        contextThreadLocal.set(new RequestContext(rqUid));
    }
    public static UUID getContextRequestId() {
        return Optional.ofNullable(contextThreadLocal.get())
                .map(RequestContext::getRequestId)
                .orElseThrow(() -> {
                    log.error("Ошибка обработки запроса, в контексте запроса отсутствует Request Id");
                    return new BitrubyRuntimeExpection("В контексте запроса отсутствует rqUid");
                });
    }

    public static void cleanContext() {
        contextThreadLocal.remove();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class RequestContext {
        private final UUID requestId;
    }

}
