/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.toolkit;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.channels.Selector;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * IOUtils Copy org.apache.commons.io.IOUtils
 *
 * @author Caratacus
 * @since 2016-11-23
 * @deprecated 3.3.0
 */
@Deprecated
public class IOUtils {

    private static final Log logger = LogFactory.getLog(IOUtils.class);

    private IOUtils() {
    }

    /**
     * Closes a URLConnection.
     *
     * @param conn the connection to close.
     * @since 2.4
     */
    public static void close(final URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }

    /**
     * Closes an <code>Reader</code> unconditionally.
     * <p>
     * Equivalent to {@link Reader#close()}, except any exceptions will be ignored. This is typically used in finally
     * blocks.
     * <p>
     * Example code:
     * <p>
     * <pre>
     * char[] data = new char[1024];
     * Reader in = null;
     * try {
     * 	in = new FileReader(&quot;foo.txt&quot;);
     * 	in.read(data);
     * 	in.close(); // close errors are handled
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(in);
     * }
     * </pre>
     * </p>
     *
     * @param input the Reader to close, may be null or already closed
     */
    public static void closeQuietly(final Reader input) {
        closeQuietly((Closeable) input);
    }

    /**
     * Closes an <code>Writer</code> unconditionally.
     * <p>
     * Equivalent to {@link Writer#close()}, except any exceptions will be ignored. This is typically used in finally
     * blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * Writer out = null;
     * try {
     * 	out = new StringWriter();
     * 	out.write(&quot;Hello World&quot;);
     * 	out.close(); // close errors are handled
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(out);
     * }
     * </pre>
     * </p>
     *
     * @param output the Writer to close, may be null or already closed
     */
    public static void closeQuietly(final Writer output) {
        closeQuietly((Closeable) output);
    }

    /**
     * Closes an <code>InputStream</code> unconditionally.
     * <p>
     * Equivalent to {@link InputStream#close()}, except any exceptions will be ignored. This is typically used in
     * finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * byte[] data = new byte[1024];
     * InputStream in = null;
     * try {
     * 	in = new FileInputStream(&quot;foo.txt&quot;);
     * 	in.read(data);
     * 	in.close(); // close errors are handled
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(in);
     * }
     * </pre>
     * </p>
     *
     * @param input the InputStream to close, may be null or already closed
     */
    public static void closeQuietly(final InputStream input) {
        closeQuietly((Closeable) input);
    }

    /**
     * Closes an <code>OutputStream</code> unconditionally.
     * <p>
     * Equivalent to {@link OutputStream#close()}, except any exceptions will be ignored. This is typically used in
     * finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * byte[] data = &quot;Hello, World&quot;.getBytes();
     *
     * OutputStream out = null;
     * try {
     * 	out = new FileOutputStream(&quot;foo.txt&quot;);
     * 	out.write(data);
     * 	out.close(); // close errors are handled
     * } catch (IOException e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(out);
     * }
     * </pre>
     * </p>
     *
     * @param output the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(final OutputStream output) {
        closeQuietly((Closeable) output);
    }

    /**
     * Closes a <code>Closeable</code> unconditionally.
     * <p>
     * Equivalent to {@link Closeable#close()}, except any exceptions will be ignored. This is typically used in finally
     * blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * Closeable closeable = null;
     * try {
     * 	closeable = new FileReader(&quot;foo.txt&quot;);
     * 	// process closeable
     * 	closeable.close();
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(closeable);
     * }
     * </pre>
     * </p>
     * <p>
     * Closing all streams:
     * </p>
     * <p>
     * <pre>
     * try {
     * 	return IOUtils.copy(inputStream, outputStream);
     * } finally {
     * 	IOUtils.closeQuietly(inputStream);
     * 	IOUtils.closeQuietly(outputStream);
     * }
     * </pre>
     * </p>
     *
     * @param closeable the objects to close, may be null or already closed
     * @since 2.0
     */
    public static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            logger.error("error close io", ioe);
        }
    }

    /**
     * Closes a <code>Closeable</code> unconditionally.
     * <p>
     * Equivalent to {@link Closeable#close()}, except any exceptions will be ignored.
     * </p>
     * <p>
     * This is typically used in finally blocks to ensure that the closeable is closed even if an Exception was thrown
     * before the normal close statement was reached. <br>
     * <b>It should not be used to replace the close statement(s) which should be present for the non-exceptional
     * case.</b> <br>
     * It is only intended to simplify tidying up where normal processing has already failed and reporting close failure
     * as well is not necessary or useful.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * Closeable closeable = null;
     * try {
     *     closeable = new FileReader(&quot;foo.txt&quot;);
     *     // processing using the closeable; may throw an Exception
     *     closeable.close(); // Normal close - exceptions not ignored
     * } catch (Exception e) {
     *     // error handling
     * } finally {
     *     <b>IOUtils.closeQuietly(closeable); // In case normal close was skipped due to Exception</b>
     * }
     * </pre>
     * </p>
     * <p>
     * Closing all streams: <br>
     * </p>
     * <p>
     * <pre>
     * try {
     * 	return IOUtils.copy(inputStream, outputStream);
     * } finally {
     * 	IOUtils.closeQuietly(inputStream, outputStream);
     * }
     * </pre>
     * </p>
     *
     * @param closeables the objects to close, may be null or already closed
     * @see #closeQuietly(Closeable)
     * @since 2.5
     */
    public static void closeQuietly(final Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (final Closeable closeable : closeables) {
            closeQuietly(closeable);
        }
    }

    /**
     * Closes a <code>Socket</code> unconditionally.
     * <p>
     * Equivalent to {@link Socket#close()}, except any exceptions will be ignored. This is typically used in finally
     * blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * Socket socket = null;
     * try {
     * 	socket = new Socket(&quot;https://www.foo.com/&quot;, 443);
     * 	// process socket
     * 	socket.close();
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(socket);
     * }
     * </pre>
     * </p>
     *
     * @param sock the Socket to close, may be null or already closed
     * @since 2.0
     */
    public static void closeQuietly(final Socket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (final IOException ioe) {
                logger.error("error close io", ioe);
            }
        }
    }

    /**
     * Closes a <code>Selector</code> unconditionally.
     * <p>
     * Equivalent to {@link Selector#close()}, except any exceptions will be ignored. This is typically used in finally
     * blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * Selector selector = null;
     * try {
     * 	selector = Selector.open();
     * 	// process socket
     *
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(selector);
     * }
     * </pre>
     * </p>
     *
     * @param selector the Selector to close, may be null or already closed
     * @since 2.2
     */
    public static void closeQuietly(final Selector selector) {
        if (selector != null) {
            try {
                selector.close();
            } catch (final IOException ioe) {
                logger.error("error close io", ioe);
            }
        }
    }

    /**
     * Closes a <code>ServerSocket</code> unconditionally.
     * <p>
     * Equivalent to {@link ServerSocket#close()}, except any exceptions will be ignored. This is typically used in
     * finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * ServerSocket socket = null;
     * try {
     * 	socket = new ServerSocket();
     * 	// process socket
     * 	socket.close();
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(socket);
     * }
     * </pre>
     * </p>
     *
     * @param sock the ServerSocket to close, may be null or already closed
     * @since 2.2
     */
    public static void closeQuietly(final ServerSocket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (final IOException ioe) {
                logger.error("error close io", ioe);
            }
        }
    }

    /**
     * Closes a <code>Connection</code> unconditionally.
     * <p>
     * Equivalent to {@link Connection#close()}, except any exceptions will be ignored. This is typically used in
     * finally blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * Connection conn = null;
     * try {
     * 	conn = new Connection();
     * 	// process close
     * 	conn.close();
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(conn);
     * }
     * </pre>
     * </p>
     *
     * @param conn the Connection to close, may be null or already closed
     * @since 2.2
     */
    public static void closeQuietly(final Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                logger.error("error close conn", e);
            }
        }
    }

    /**
     * Closes a <code>AutoCloseable</code> unconditionally.
     * <p>
     * Equivalent to {@link ResultSet#close()}, except any exceptions will be ignored. This is typically used in finally
     * blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * AutoCloseable statement = null;
     * try {
     * 	statement = new Connection();
     * 	// process close
     * 	statement.close();
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(conn);
     * }
     * </pre>
     * </p>
     *
     * @param resultSet the Connection to close, may be null or already closed
     * @since 2.2
     */
    public static void closeQuietly(final ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) {
                logger.error("error close resultSet", e);
            }
        }
    }

    /**
     * Closes a <code>AutoCloseable</code> unconditionally.
     * <p>
     * Equivalent to {@link Statement#close()}, except any exceptions will be ignored. This is typically used in finally
     * blocks.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * AutoCloseable statement = null;
     * try {
     * 	statement = new Connection();
     * 	// process close
     * 	statement.close();
     * } catch (Exception e) {
     * 	// error handling
     * } finally {
     * 	IOUtils.closeQuietly(conn);
     * }
     * </pre>
     * </p>
     *
     * @param statement the Connection to close, may be null or already closed
     * @since 2.2
     */
    public static void closeQuietly(final Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                logger.error("error close statement", e);
            }
        }
    }

    /**
     * Closes a <code>AutoCloseable</code> unconditionally.
     * <p>
     * Equivalent to {@link AutoCloseable#close()}, except any exceptions will be ignored.
     * </p>
     * <p>
     * This is typically used in finally blocks to ensure that the closeable is closed even if an Exception was thrown
     * before the normal close statement was reached. <br>
     * <b>It should not be used to replace the close statement(s) which should be present for the non-exceptional
     * case.</b> <br>
     * It is only intended to simplify tidying up where normal processing has already failed and reporting close failure
     * as well is not necessary or useful.
     * </p>
     * <p>
     * Example code:
     * </p>
     * <p>
     * <pre>
     * AutoCloseable closeable = null;
     * try {
     *     closeable = new AutoCloseable();
     *     // processing using the closeable; may throw an Exception
     *     closeable.close(); // Normal close - exceptions not ignored
     * } catch (Exception e) {
     *     // error handling
     * } finally {
     *     <b>IOUtils.closeQuietly(closeable); // In case normal close was skipped due to Exception</b>
     * }
     * </pre>
     * </p>
     * <p>
     * Closing all streams: <br>
     * </p>
     *
     * @param statements the objects to close, may be null or already closed
     * @see #closeQuietly(Statement)
     * @since 2.5
     */
    public static void closeQuietly(final Statement... statements) {
        if (statements == null) {
            return;
        }
        for (final Statement statement : statements) {
            closeQuietly(statement);
        }
    }

}
