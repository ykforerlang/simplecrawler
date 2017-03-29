package com.ykfirst.simplecrawler.web;

import com.ykfirst.simplecrawler.core.Task;

/**
 *download static resource . object : urlfile
 *@author yankang
 *@date 2015年1月8日
 */
public class StaticResourceTask extends Task<SiteInfo> {
	private final String file;
	private final String url;

	public StaticResourceTask(String url, String file) {
		this.file = file;
		this.url = url;
	}


	public String getType() {
		return null;
	}
	public SiteInfo getDate() {
		return null;
	}
	public String getFilePath() {
		return file;
	}
	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return "StaticResourceTask [url=" + url + ", file=" + file + "]";
	}
}
