package app.fyp.besecure.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import app.fyp.besecure.PhoneModel.ImageUploadModel;
import app.fyp.besecure.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends Fragment {

    //    int images[]={R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};
    private ViewFlipper simpleViewFlipper;

    private ArrayList<String> mCategories = new ArrayList<>();


    int countInt, incrementalCount;
    DatabaseReference ProductReference;
    RecyclerView mProductRecycVw;
    String count;

    public DashBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);
        ProductReference = FirebaseDatabase.getInstance().getReference().child("ImagesUrl").child(FirebaseAuth.getInstance().getUid());
        getActivity().setTitle("Images");


        mProductRecycVw = view.findViewById(R.id.recycler_vw_catagory);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mProductRecycVw.setLayoutManager(mLayoutManager);        // loop for creating ImageView's

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ImageUploadModel> options = new FirebaseRecyclerOptions.Builder<ImageUploadModel>()
                .setQuery(ProductReference, ImageUploadModel.class)
                .build();

        FirebaseRecyclerAdapter<ImageUploadModel, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<ImageUploadModel, DashBoardFragment.ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final DashBoardFragment.ProductViewHolder holder, int position, @NonNull ImageUploadModel model) {


                DisplayMetrics displaymetrics = new DisplayMetrics();
                (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                //if you need three fix imageview in width

                Glide.with(getActivity().getApplicationContext()).load(model.getImageurl()).into(holder.postImage);



            }

            @NonNull
            @Override
            public DashBoardFragment.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_item_layout, viewGroup, false);
                DashBoardFragment.ProductViewHolder productViewHolder = new DashBoardFragment.ProductViewHolder(view);
                return productViewHolder;
            }
        };

        mProductRecycVw.setAdapter(adapter);
        adapter.startListening();
        int firstCount=adapter.getItemCount();
        adapter.notifyDataSetChanged();

        int count =adapter.getItemCount();

        if ((count!=firstCount)){
            Toast.makeText(getContext(), "data changed", Toast.LENGTH_SHORT).show();
        }

    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {


        ImageView postImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.imageView);


        }
    }
}

