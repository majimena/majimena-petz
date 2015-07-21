package org.majimena.petz.web.servlet.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

/**
 * グローバルな例外ハンドラ.<br>
 * 例外処理は全てここで完結させるので各コントローラでは何もせずにスローする.
 */
@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    private AppError systemAppError = new AppError("CMM-E-999999", "Internal Server Error.", "");
//
//    @ExceptionHandler(BindException.class)
//    public ResponseEntity<List<AppError>> handleBindException(BindException e) {
//        logger.info("BindException exception has occured. BindException=[" + e.toString() + "]");
//
//        // FIXME メッセージリソースから読んだ方がいいだろう
//        List<ObjectError> errors = e.getAllErrors();
//        List<AppError> body = errors.stream()
//            .map(error -> new AppError(error.getCode(), error.getDefaultMessage(), ""))
//            .collect(Collectors.toList());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
//    }
//
//    @ExceptionHandler(Throwable.class)
//    public ResponseEntity<List<AppError>> handleException(Throwable e) {
//        logger.error("Unhandle exception has occured.", e);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Arrays.asList(systemAppError));
//    }

//    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ApplicationMessages handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        List<ObjectError> errors = e.getBindingResult().getAllErrors();
//        List<ApplicationMessage> messages = new ArrayList<>();
//        errors.forEach(error -> messages.add(new ApplicationMessage(error)));
//        return new ApplicationMessages(messages);
//    }

}
