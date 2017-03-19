package com.reward.resources;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reward.model.CallBackInfo;
import com.reward.util.HttpClientUtil;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gege on 2017/3/19.
 */
public class MusicResource {
    private static MusicResource instance;

    public static MusicResource getInstance() {
        if (instance == null)
            instance = new MusicResource();
        return instance;
    }

    private MusicResource() {

    }

    public String getMuiscPath(String content) {
        String result = doPost(buildParams(content));
//        String result = "{\"result\":{\"songs\":[{\"name\":\"夜空中最亮的星\",\"id\":25706282,\"position\":0,\"alias\":[],\"status\":0,\"fee\":0,\"copyrightId\":5003,\"disc\":\"\",\"no\":7,\"artists\":[{\"name\":\"逃跑计划\",\"id\":12977,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p3.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":\"http://p3.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0}],\"album\":{\"name\":\"世界\",\"id\":2285010,\"type\":\"专辑\",\"size\":10,\"picId\":1744924953300592,\"blurPicUrl\":\"http://p3.music.126.net/9l6BFNA9Kiyu2thC9Jt_LA==/1744924953300592.jpg\",\"companyId\":0,\"pic\":1744924953300592,\"picUrl\":\"http://p3.music.126.net/9l6BFNA9Kiyu2thC9Jt_LA==/1744924953300592.jpg\",\"publishTime\":1325347200007,\"description\":\"\",\"tags\":\"\",\"company\":\"美丽世界音乐\",\"briefDesc\":\"\",\"artist\":{\"name\":\"\",\"id\":0,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p3.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":\"http://p3.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0},\"songs\":[],\"alias\":[],\"status\":1,\"copyrightId\":5003,\"commentThreadId\":\"R_AL_3_2285010\",\"artists\":[{\"name\":\"逃跑计划\",\"id\":12977,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p3.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":\"http://p3.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0}]},\"starred\":false,\"popularity\":100.0,\"score\":100,\"starredNum\":0,\"duration\":252000,\"playedNum\":0,\"dayPlays\":0,\"hearTime\":0,\"ringtone\":\"600902000009535440\",\"crbt\":null,\"audition\":null,\"copyFrom\":\"\",\"commentThreadId\":\"R_SO_4_25706282\",\"rtUrl\":null,\"ftype\":0,\"rtUrls\":[],\"copyright\":1,\"hMusic\":{\"name\":null,\"id\":99233342,\"size\":10091800,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":3293037325254134,\"bitrate\":320000,\"playTime\":252000,\"volumeDelta\":-0.37},\"mMusic\":{\"name\":null,\"id\":99233343,\"size\":5045989,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":7938473954080486,\"bitrate\":160000,\"playTime\":252000,\"volumeDelta\":-2.65076E-4},\"lMusic\":{\"name\":null,\"id\":99233344,\"size\":3027664,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":3255653929909959,\"bitrate\":96000,\"playTime\":252000,\"volumeDelta\":-0.04},\"bMusic\":{\"name\":null,\"id\":99233344,\"size\":3027664,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":3255653929909959,\"bitrate\":96000,\"playTime\":252000,\"volumeDelta\":-0.04},\"mvid\":382555,\"rtype\":0,\"rurl\":null,\"mp3Url\":\"http://m2.music.126.net/r22btSztTs6hPyS-SNYJSg==/3255653929909959.mp3\"}],\"songCount\":135},\"code\":200}";
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (200 == jsonObject.getInteger("code")) {
                jsonObject = jsonObject.getJSONObject("result");
                JSONArray jsonArray = jsonObject.getJSONArray("songs");
                if (jsonArray != null && jsonArray.size() > 0) {
                    jsonObject = jsonArray.getJSONObject(0);
                    jsonObject = jsonObject.getJSONObject("album");
                    if (jsonObject.containsKey("picUrl")) {
                        result = jsonObject.getString("picUrl");
                    }
                }

            }
        }
        return StringUtils.isNotBlank(result) ? result : "该曲目专辑图片不存在";
    }

    private Map<String, String> buildParams(String content) {
        Map<String, String> musicReq = new HashMap<>();
        musicReq.put("s", content);
        musicReq.put("offset", "0");
        musicReq.put("limit", "1");
        musicReq.put("type", "1");
        return musicReq;
    }

    private String doPost(Map<String, String> musicReq) {
        try {
            CallBackInfo callBackInfo = HttpClientUtil.sendPost("http://music.163.com/api/search/pc", musicReq, null);
            if (200 == callBackInfo.getCode()) {
                return callBackInfo.getContent();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(MusicResource.getInstance().getMuiscPath("夜空中最亮的星"));
    }
}
