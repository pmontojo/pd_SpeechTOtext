/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author diana and paula
 */
public class Video {

        int id;
        String url = null;
        String course_name = null;
        String video_name = null;
        
        public Video(){};
	public Video( int id,String url,String course_name,String video_name) {
            
		this.id = id;
		this.url = url;
		this.course_name = course_name;
		this.video_name = video_name;
                
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setUrl(String url) {
		this.url = url;

	}

	public String getUrl() {
		return url;
	}

	public String getCourseName() {
		return course_name;
	}

	public void setCourseName(String course_name) {
		this.course_name = course_name;
	}

	public String getVideoName() {
		return video_name;
	}

	public void setVideoName(String video_name) {
		this.video_name = video_name;
	}
}

