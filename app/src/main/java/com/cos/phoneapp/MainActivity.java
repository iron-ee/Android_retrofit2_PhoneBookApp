package com.cos.phoneapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    private RecyclerView rvPhone;
    private PhoneAdapter phoneAdapter;
    private FloatingActionButton fab;
    private List<Phone> phones = new ArrayList<>();

    private PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();

        findAll();

        fab.setOnClickListener(v -> {
            add();
        });
    }

    public void init() {
        rvPhone = findViewById(R.id.rv_phone);
        fab = findViewById(R.id.fab_save);
    }


    public void createTel(String name, String tel) {
        Phone phone = new Phone(null, name, tel);
        Call<CMRespDto<Phone>> call = phoneService.save(phone);

        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<Phone> cmRespDto = response.body();
                Phone phone = cmRespDto.getData();
                phones.add(phone);
                phoneAdapter.notifyDataSetChanged();
                Log.d(TAG, "onResponse: 등록 성공 !" +phone);
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 등록 실패 !");
            }
        });
    }
    
    public void updateTel(String name, String tel, int position, long id) {
        Phone phone = new Phone(null, name, tel);
        Call<CMRespDto <Phone>> call = phoneService.update(id, phone);
        
        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                CMRespDto<Phone> cmRespDto = response.body();
                Phone phone = cmRespDto.getData();
                phones.set(position, phone);
                phoneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 수정 실패");
            }
        });
    }
    
    public void deleteTel(int position, long id) {
        Call<CMRespDto<Phone>> call = phoneService.delete(id);
        
        call.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                phones.remove(position);
                phoneAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onFailure: 삭제 실패");
            }
        });
    }

    public void findAll() {
        Call<CMRespDto<List<Phone>>> call = phoneService.findAll();
        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();
                phones = cmRespDto.getData();
                // 어댑터에게 넘기기
                phoneAdapter = new PhoneAdapter(MainActivity.this, phones);
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                rvPhone.setLayoutManager(layoutManager);
                rvPhone.setAdapter(phoneAdapter);
                Log.d(TAG, "onResponse: 응답 받은 데이터 : "+phones);
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "onFailure: findAll() 실패 ");
            }
        });
    }

    public void add() {
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_item, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etTel = dialogView.findViewById(R.id.et_tel);

        AlertDialog.Builder dig = new AlertDialog.Builder(MainActivity.this);
        dig.setTitle("새 연락처 등록");
        dig.setView(dialogView);
        dig.setPositiveButton("등록", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createTel(etName.getText().toString(), etTel.getText().toString());
            }
        });
        dig.setNegativeButton("닫기", null);
        dig.show();
    }


    public void update(int position, Phone phone) {
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_item, null);

        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etTel = dialogView.findViewById(R.id.et_tel);

        if (phone != null) {
            etName.setText(phone.getName());
            etTel.setText(phone.getTel());
        }
        long id = (phone.getId()).intValue();

        AlertDialog.Builder dig = new AlertDialog.Builder(MainActivity.this);
        dig.setTitle("연락처 수정");
        dig.setView(dialogView);
        dig.setPositiveButton("수정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateTel(etName.getText().toString(), etTel.getText().toString(), position, id);
            }
        });
        dig.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTel(position, id);
            }
        });
        dig.show();
    }

}