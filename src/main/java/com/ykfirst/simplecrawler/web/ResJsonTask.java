package com.ykfirst.simplecrawler.web;

import com.ykfirst.simplecrawler.core.Task;

/**
 *download static resource . object : urlfile
 *@author yankang
 *@date 2015年1月8日
 */
public class ResJsonTask extends Task<SiteInfo> {
	public ResJsonTask(String type, SiteInfo date) {
		super(type, date);
	}
}
