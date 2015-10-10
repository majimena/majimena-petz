package org.majimena.petz.web.servlet.filter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.majimena.petz.common.factory.JsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * アクセスログフィルタ.
 */
public class AccessLogFilter extends OncePerRequestFilter {
    /**
     * ログ.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

    /**
     * JSONファクトリ.
     */
    private JsonFactory jsonFactory;

    /**
     * JSONファクトリ.
     *
     * @param jsonFactory JSONファクトリ
     */
    public void setJsonFactory(final JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // ログ出力用パラメータを取得
        final String url = request.getRequestURL().toString();
        final String method = request.getMethod();
        final LoggingHttpServletRequest req = new LoggingHttpServletRequest(request);
        final LoggingHttpServletResponse res = new LoggingHttpServletResponse(response);
        final StopWatch watch = new StopWatch();

        try {
            // 処理開始ログの出力
            LOGGER.info("[{}] [{}] is starting.", method, url);
            if (LOGGER.isDebugEnabled()) {
                String params = jsonFactory.to(req.getParameterMap());
                String body;
                try (BufferedReader reader = req.getReader()) {
                    body = reader.readLine();
                }
                LOGGER.debug("[{}] [{}] request params: {}", method, url, params);
                LOGGER.debug("[{}] [{}] request body: {}", method, url, body);
            }

            // 実処理を実行する
            watch.start();
            chain.doFilter(req, res);
        } finally {
            // 処理計測の終了と処理完了ログの出力
            watch.stop();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[{}] [{}] [{}] response: {}", method, url, res.getStatus(), res.getStringResponse());
            }
            LOGGER.info("[{}] [{}] has ended. Process time is [{}ms]", method, url, watch.getTime());
        }
    }

    /**
     * ログ出力用サーブレットリクエストの実装クラス.
     */
    private static class LoggingHttpServletRequest extends HttpServletRequestWrapper {
        /**
         * リクエストボディのバイト.
         */
        private byte[] buffer;

        /**
         * オリジナルのサーブレットインプットストリーム.
         */
        private ServletInputStream original;

        /**
         * コンストラクタ.
         *
         * @param request サーブレットリクエスト
         */
        public LoggingHttpServletRequest(HttpServletRequest request) {
            super(request);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (buffer == null) {
                original = getRequest().getInputStream();
                buffer = IOUtils.toByteArray(original);
            }
            return new LoggingServletInputStream(original, buffer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        /**
         * ログ出力するためにラップしたサーブレットインプットストリーム.
         */
        private static class LoggingServletInputStream extends ServletInputStream {
            /**
             * オリジナルのサーブレットインプットストリーム.
             */
            private ServletInputStream original;

            /**
             * ログ出力用にラップしたインプットストリーム.
             */
            private InputStream inputStream;

            /**
             * コンストラクタ.
             *
             * @param inputStream オリジナルのサーブレットインプットストリーム
             * @param buffer      ラップしたインプットストリームを作成するためのリクエストボディ情報
             */
            public LoggingServletInputStream(ServletInputStream inputStream, byte[] buffer) {
                this.original = inputStream;
                this.inputStream = new ByteArrayInputStream(buffer);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isFinished() {
                return original.isFinished();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isReady() {
                return original.isReady();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void setReadListener(final ReadListener readListener) {
                original.setReadListener(readListener);
            }
        }
    }

    /**
     * ログ出力用サーブレットレスポンスの実装クラス.
     */
    private static class LoggingHttpServletResponse extends HttpServletResponseWrapper {
        /**
         * ログ出力用にラップしたアウトブットストリーム.
         */
        private LoggingServletOutputStream outputStream;

        /**
         * プリントライタ.
         */
        private PrintWriter writer;

        /**
         * コンストラクタ.
         *
         * @param response オリジナルのサーブレットレスポンス
         */
        public LoggingHttpServletResponse(HttpServletResponse response) {
            super(response);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (outputStream == null) {
                outputStream = new LoggingServletOutputStream(getResponse().getOutputStream());
            }
            return outputStream;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PrintWriter getWriter() throws IOException {
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), getResponse().getCharacterEncoding()), true);
            }
            return writer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void flushBuffer() throws IOException {
            if (writer != null) {
                writer.flush();
            }
            if (outputStream != null) {
                outputStream.flush();
            }
            super.flushBuffer();
        }

        /**
         * ログ出力用のアウトプットストリームを文字列にします.
         *
         * @return レスポンスボディを文字列変換したもの
         */
        public String getStringResponse() {
            if (outputStream != null) {
                return outputStream.getStringResponse();
            } else {
                return "";
            }
        }

        /**
         * ログ出力用にラップしたアウトプットストリーム.
         */
        private static class LoggingServletOutputStream extends ServletOutputStream {
            /**
             * オリジナルのアウトプットストリーム.
             */
            private ServletOutputStream original;

            /**
             * ログ出力用のアウトプットストリーム.
             */
            private ByteArrayOutputStream buffer;

            /**
             * コンストラクタ.
             *
             * @param outputStream オリジナルのアウトプットストリーム
             */
            public LoggingServletOutputStream(ServletOutputStream outputStream) {
                this.original = outputStream;
                this.buffer = new ByteArrayOutputStream(1024);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void write(int b) throws IOException {
                original.write(b);
                buffer.write(b);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean isReady() {
                return original.isReady();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void setWriteListener(final WriteListener writeListener) {
                original.setWriteListener(writeListener);
            }

            /**
             * ログ出力用のアウトプットストリームを文字列にします.
             *
             * @return レスポンスボディを文字列変換したもの
             */
            public String getStringResponse() {
                return buffer.toString();
            }
        }
    }
}
