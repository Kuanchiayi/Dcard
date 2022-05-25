package com.example.btnaddtab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    FavAdapter adapter;
    SharedPreferences pref;

    String title, forumName, comment, like, excerpt, id, gender, jsonArray, final_list_id;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    ArrayList<JSONObject> final_list = new ArrayList<>();
    ArrayList<String> id_list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);
        initViews();
        readData();
        return view;
    }

    public void initViews(){
        recyclerView = view.findViewById(R.id.recyclerView_favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter = new FavAdapter(getActivity(), arrayList);
        recyclerView.setAdapter(adapter);
    }

    public void readData(){
        pref = getActivity().getSharedPreferences("example", MODE_PRIVATE);
        String jsonArray = pref.getString("jsonArray", "");

        try {
            /*解析jsonArray*/
            JSONArray array = new JSONArray(jsonArray);
            for (int i=0; i<array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                id = jsonObject.getString("id");
                forumName = jsonObject.getString("forumName");
                title = jsonObject.getString("title");
                excerpt = jsonObject.getString("excerpt");
                like = jsonObject.getString("likeCount");
                comment = jsonObject.getString("commentCount");
                gender = jsonObject.getString("gender");
                id = jsonObject.getString("id");

                /*放入Array*/
                HashMap<String, String> data = new HashMap<>();
                data.put("title", title);
                data.put("forumName", forumName);
                data.put("excerpt", excerpt);
                data.put("like", like);
                data.put("comment", comment);
                data.put("gender", gender);
                data.put("id", id);
                arrayList.add(data);
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class FavAdapter extends RecyclerView.Adapter<FavoriteFragment.FavAdapter.ViewHolder>{

        private Context context;
        public ArrayList<HashMap<String,String>> arrayList;
        public FavAdapter(Context context, ArrayList<HashMap<String,String>> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView tv_title, tv_forumName, tv_excerpt, favorite_count, comment_count, tv_id, tv_gender;
            ImageView iv_avatar, iv_thumbnail, iv_cancel;

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
                iv_cancel = itemsView.findViewById(R.id.iv_cancel);
                tv_gender = itemsView.findViewById(R.id.tv_gender);

                /*        點選事件放在adapter中使用，也可以寫個介面在activity中呼叫        */
                itemsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ArticleActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("forumAlias", String.valueOf(tv_forumName.getText()));
                        bundle.putString("id", String.valueOf(tv_id.getText()));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                iv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("是否取消收藏")
                                .setPositiveButton("ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (final_list.size()>0) {
                                                    for (int i=0; i<final_list.size(); i++) {
                                                        adapter.notifyItemRemoved(i);
                                                        adapter.notifyItemRangeChanged(0, final_list.size()-1);
                                                    }
                                                }
                                                /*刪除資料*/
                                                collect_items();
                                                /*重新讀取jsonArray*/
                                                readData();
                                            }
                                        })
                                .show();
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
                    /*取出原本的收藏項目*/
                    JSONArray originJsonArray = new JSONArray(jsonArray);
                    for (int i=0; i< originJsonArray.length(); i++) {
                        JSONObject originJsonObj = originJsonArray.getJSONObject(i);
                        final_list.add(originJsonObj);

                        String origin_id = originJsonObj.getString("id");
                        id_list.add(origin_id);
                    }
                    Log.e("id_list:", id_list+"a");

                    JSONArray final_list_array = new JSONArray(final_list);
                    for (int j=0; j<final_list_array.length(); j++) {
                        JSONObject final_list_obj = final_list_array.getJSONObject(j);
                        final_list_id = final_list_obj.getString("id");
                        if (tv_id.getText().equals(final_list_id)) {
                            final_list.remove(j);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("id_list:", id_list+"a");
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
        public FavAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_fav_item, parent, false);
            return new FavAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FavAdapter.ViewHolder holder, int position) {
            HashMap<String,String> data = arrayList.get(position);
            holder.tv_forumName.setText(data.get("forumName"));
            holder.tv_title.setText(data.get("title"));
            holder.tv_excerpt.setText(data.get("excerpt"));
            holder.favorite_count.setText(data.get("like"));
            holder.comment_count.setText(data.get("comment"));
            holder.tv_id.setText(data.get("id"));

            if (data.get("gender").equals("M")) {
                holder.iv_avatar.setImageResource(R.drawable.male);
            } else if (data.get("gender").equals("F")) {
                holder.iv_avatar.setImageResource(R.drawable.female);
            }

//            if (!"".equals(data.get("thumbnailUrl"))) {
//                Glide.with(getActivity()).load(data.get("thumbnailUrl")).into(holder.iv_thumbnail);
//            } else {
//                holder.iv_thumbnail.setImageResource(0);
//            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
}