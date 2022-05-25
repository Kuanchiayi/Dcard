package com.example.btnaddtab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.tech.NfcA;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.WeakHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class NewestFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    NewAdapter adapter;
    ArrayAdapter arrayAdapter;
    Spinner spinner;
    SharedPreferences pref;

    Boolean firstTime = true;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    ArrayList<HashMap<String, String>>  collect_list = new ArrayList<>();
    ArrayList<JSONObject> final_list = new ArrayList<>();
    ArrayList<String> id_list = new ArrayList<>();
    HashMap<String,String> fav_hashMap;
    String title, result, excerpt, forumName, like, comment, school, gender, post_avatarUrl, thumbnailUrl, forumAlias, url, jsonArray, final_list_id;
    int id;
    public NewestFragment(String url){
        this.url = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newest, container, false);
        initViews();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendGet(url);
    }

    public void initViews(){
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView_new);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter = new NewAdapter(getActivity(), arrayList);
        recyclerView.setAdapter(adapter);
    }

    public void sendGet(String url){
        progressBar.setVisibility(View.VISIBLE);

        /*   建立連線   */
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))  //Log資訊
                .build();
        /*   傳送需求  */
        Request request = new Request.Builder()
                .url(url)
                .build();

        /*   設置回傳  */
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                /*如果傳送過程有發生錯誤*/
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                /*取得回傳*/
                result = response.body().string();
                System.out.println(result);
                arrayList.clear();
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        forumName = jsonObject.getString("forumName");
                        title = jsonObject.getString("title");
                        excerpt = jsonObject.getString("excerpt");
                        like = jsonObject.getString("likeCount");
                        comment = jsonObject.getString("commentCount");
                        forumAlias = jsonObject.getString("forumAlias");
                        id = jsonObject.getInt("id");
                        gender = jsonObject.getString("gender");

                        /*     判斷&取得頭貼url      */
                        if (jsonObject.has("postAvatar")) {
                            post_avatarUrl = jsonObject.getString("postAvatar");
                        } else {
                            post_avatarUrl = "";
                        }
                        /*   判斷是否為匿名   */
                        if(jsonObject.has("school")) {
                            school = jsonObject.getString("school");
                        } else {
                            school = "匿名";
                        };

                        /*     判斷&取得縮圖url      */
                        JSONArray mediaMeta_array = jsonObject.getJSONArray("mediaMeta");
                        if (mediaMeta_array.length()>0) {
                            thumbnailUrl = mediaMeta_array.getJSONObject(0).getString("url");
                        } else {
                            thumbnailUrl = "";
                        }

                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("title", title);
                        hashMap.put("forumName", forumName);
                        hashMap.put("excerpt", excerpt);
                        hashMap.put("like", like);
                        hashMap.put("comment", comment);
                        hashMap.put("gender", gender);
                        hashMap.put("postAvatar", post_avatarUrl);
                        hashMap.put("school", school);
                        hashMap.put("thumbnailUrl", thumbnailUrl);
                        hashMap.put("id", String.valueOf(id));
                        arrayList.add(hashMap);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(() -> {
                    /*更新Adapter*/
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }
        });
    }

    /*            RecyclerAdapter           */
    public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder>{

        private Context context;
        public ArrayList<HashMap<String,String>> arrayList;
        public NewAdapter(Context context, ArrayList<HashMap<String,String>> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView tv_title, tv_forumName, tv_excerpt, favorite_count, comment_count, tv_id, tv_gender;
            ImageView iv_avatar, iv_thumbnail;
            ImageButton btn_collect;

            public ViewHolder(View itemsView){
                super(itemsView);
                tv_title = itemsView.findViewById(R.id.tv_title);
                tv_forumName = itemsView.findViewById(R.id.tv_forumName);
                tv_excerpt = itemsView.findViewById(R.id.tv_excerpt);
                favorite_count = itemsView.findViewById(R.id.favorite_count);
                comment_count = itemsView.findViewById(R.id.comment_count);
                iv_avatar = itemsView.findViewById(R.id.img_1);
                tv_id = itemsView.findViewById(R.id.tv_id);
                iv_thumbnail = itemsView.findViewById(R.id.iv_thumbnail);
                btn_collect = itemsView.findViewById(R.id.collect);
                spinner = itemsView.findViewById(R.id.spinner);
                tv_gender = itemsView.findViewById(R.id.tv_gender);

                /*        點選事件放在adapter中使用，也可以寫個介面在activity中呼叫        */
                itemsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ArticleActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("forumAlias", String.valueOf(tv_forumName.getText()));
                        bundle.putInt("id", Integer.parseInt(String.valueOf(tv_id.getText())));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                spinner.setSelection(2,false);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        if (firstTime) {
                            firstTime = false;
                        } else if (position == 1) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, tv_title.getText());
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            startActivity(shareIntent);
                            adapterView.setSelection(2);
                        } else {
//                            Toast.makeText(getActivity(), "collect", Toast.LENGTH_SHORT).show();
                            adapterView.setSelection(2);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                btn_collect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       collect_items();
                    }
                });
            }

            private void collect_items() {
                /*取出原本data*/
                pref = getActivity().getSharedPreferences("example", MODE_PRIVATE);
                jsonArray = pref.getString("jsonArray", "");
                final_list.clear();
                id_list.clear();
                try {
                    JSONArray originJsonArray = new JSONArray(jsonArray);
                    for (int i=0; i< originJsonArray.length(); i++) {
                        JSONObject originJsonObj = originJsonArray.getJSONObject(i);
                        final_list.add(originJsonObj);

                        String origin_id = originJsonObj.getString("id");
                        id_list.add(origin_id);
                    }
                    Log.e("id_list:", id_list+"a");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*  將data存到array裡  */
                collect_list.clear();
                fav_hashMap = new HashMap<>();
                fav_hashMap.put("id", String.valueOf(tv_id.getText()));
                fav_hashMap.put("title", String.valueOf(tv_title.getText()));
                fav_hashMap.put("forumName", String.valueOf(tv_forumName.getText()));
                fav_hashMap.put("excerpt", String.valueOf(tv_excerpt.getText()));
                fav_hashMap.put("likeCount", String.valueOf(favorite_count.getText()));
                fav_hashMap.put("commentCount", String.valueOf(comment_count.getText()));
                fav_hashMap.put("gender", String.valueOf(tv_gender.getText()));
                /*新增的會放進collect_list 轉成jsonArray*/
                collect_list.add(fav_hashMap);
                JSONArray collect_jsonArray = new JSONArray(collect_list);

                for (int i=0; i < collect_jsonArray.length(); i++) {
                    try {
                        JSONObject newJsonObj = collect_jsonArray.getJSONObject(i);
                        String new_id = newJsonObj.getString("id");
                        Log.e("new_id", new_id+"a");

                        /*用id來判斷有沒有在收藏裡*/
                        if (!id_list.contains(new_id)) {
                            final_list.add(newJsonObj);
                            /* 收藏變色 */
                            btn_collect.setBackgroundColor(R.drawable.bookmark);
                        } else {
                            /*取消收藏*/
                            JSONArray final_list_array = new JSONArray(final_list);
                            for (int j=0; j<final_list_array.length(); j++) {
                                JSONObject final_list_obj = final_list_array.getJSONObject(j);
                                final_list_id = final_list_obj.getString("id");
                                if (new_id.equals(final_list_id)) {
                                    final_list.remove(j);
//                                    btn_collect.setImageResource(R.drawable.round_bookmark_24);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.e("jsonArray", collect_jsonArray.toString() + "a");
                Log.e("size:", collect_jsonArray.length() + "a");
                Log.e("final_list:", final_list +"a");
                Log.e("size:", final_list.size() + "a");


                /*      寫入sharedPreference     */
//                        pref = getActivity().getSharedPreferences("example", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
//                        editor.clear();
                editor.putString("jsonArray", final_list.toString());
                editor.apply();
            }
        }

        @NonNull
        @Override
        public NewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_items, parent, false);
            return new NewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewAdapter.ViewHolder holder, int position) {
            HashMap<String,String> data = arrayList.get(position);
            holder.btn_collect.setImageResource(R.drawable.round_bookmark_24);
            holder.tv_forumName.setText(String.format("%s ・ %s", data.get("forumName"), data.get("school")));
            holder.tv_title.setText(data.get("title"));
            holder.tv_excerpt.setText(data.get("excerpt"));
            holder.favorite_count.setText(data.get("like"));
            holder.comment_count.setText(data.get("comment"));
            holder.tv_id.setText(data.get("id"));
            holder.tv_gender.setText(data.get("gender"));

            if (data.get("gender").equals("M")) {
                holder.iv_avatar.setImageResource(R.drawable.male);
            } else if (data.get("gender").equals("F")) {
                holder.iv_avatar.setImageResource(R.drawable.female);
            } else {
                Glide.with(getActivity()).load(post_avatarUrl).into(holder.iv_avatar);
            }

            if (!"".equals(data.get("thumbnailUrl"))) {
                Glide.with(getActivity()).load(data.get("thumbnailUrl")).into(holder.iv_thumbnail);
            } else {
                holder.iv_thumbnail.setImageResource(0);
            }

            arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.menu_array, android.R.layout.simple_dropdown_item_1line);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }


}