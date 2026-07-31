/*#######################################################
 *
 * SPDX-FileCopyrightText: 2017-2025 Gregor Santner <gsantner AT mailbox DOT org>
 * SPDX-License-Identifier: Unlicense OR CC0-1.0
 *
 * Written 2022-2025 by Gregor Santner <gsantner AT mailbox DOT org>
 * To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 * You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
#########################################################*/
package net.gsantner.opoc.web;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class GsWebViewClient extends WebViewClient {
    protected final WeakReference<WebView> m_webView;

    public GsWebViewClient(final WebView webView) {
        m_webView = new WeakReference<>(webView);
    }

    @Override
    public void onPageFinished(final WebView webView, final String url) {
        __onPageFinished_restoreScrollY(webView, url);
        __onPageFinished_restoreScrollYPercent(webView, url);
        super.onPageFinished(webView, url);
    }

    /// /////////////////////////////////////////////////////////////////////////////////
    private final AtomicBoolean m_restoreScrollYEnabled = new AtomicBoolean(false);
    private int m_restoreScrollY = 0;

    /**
     * Activate by {@link GsWebViewClient#setRestoreScrollY(int)}
     *
     * @param webView onPageFinished {@link WebView}
     * @param url     onPageFinished url
     */
    protected void __onPageFinished_restoreScrollY(final WebView webView, final String url) {
        if (m_restoreScrollYEnabled.getAndSet(false)) {
            for (int dt : new int[]{50, 100, 150, 200, 250, 300}) {
                webView.postDelayed(() -> webView.setScrollY(m_restoreScrollY), dt);
            }
        }
    }

    /**
     * Apply vertical scroll position on next page load
     *
     * @param scrollY scroll position from {@link WebView#getScrollY()}
     */
    public void setRestoreScrollY(final int scrollY) {
        m_restoreScrollY = scrollY;
        m_restoreScrollYEnabled.set(scrollY >= 0);
    }

    /// /////////////////////////////////////////////////////////////////////////////////
    private final AtomicBoolean m_restoreScrollYPercentEnabled = new AtomicBoolean(false);
    private float m_restoreScrollYPercent = 0f;

    /**
     * Activate by {@link GsWebViewClient#setRestoreScrollYPercent(float)}
     *
     * @param webView onPageFinished {@link WebView}
     * @param url     onPageFinished url
     */
    protected void __onPageFinished_restoreScrollYPercent(final WebView webView, final String url) {
        if (m_restoreScrollYPercentEnabled.getAndSet(false)) {
            final String js = "window.scrollTo(0, " + m_restoreScrollYPercent
                    + " * (document.documentElement.scrollHeight - window.innerHeight));";
            for (int dt : new int[]{50, 100, 150, 200, 250, 300}) {
                webView.postDelayed(() -> webView.evaluateJavascript(js, null), dt);
            }
        }
    }

    /**
     * Scroll to a fraction of the page's scrollable height on next page load.
     * Unlike {@link #setRestoreScrollY(int)}, this is independent of the page's
     * content height, so it applies correctly even when the content changed since
     * the fraction was captured (e.g. syncing scroll position from a different view).
     *
     * @param percent fraction (0..1) of the page's scrollable height
     */
    public void setRestoreScrollYPercent(final float percent) {
        m_restoreScrollYPercent = percent;
        m_restoreScrollYPercentEnabled.set(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////
}
