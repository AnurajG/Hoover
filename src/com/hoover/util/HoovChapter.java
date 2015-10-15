package com.hoover.util;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class HoovChapter implements Serializable {
		public String hoovText;
		public String hoovDate;
		public String mongoHoovId;
		public Boolean posted;
		public String hoovUserId;
		public ArrayList<String> hoov_up_ids;
		public ArrayList<String> hoov_down_ids;
		public ArrayList<String> commentHoovIds;
		public Boolean followed;
		public Boolean abused;
		//public String path;
		

}
