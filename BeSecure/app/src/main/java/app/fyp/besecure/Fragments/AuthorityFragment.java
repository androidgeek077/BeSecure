package app.fyp.besecure.Fragments;


import android.content.Intent;
import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
        import android.util.DisplayMetrics;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
        import android.widget.ViewFlipper;

        import com.bumptech.glide.Glide;
        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import com.firebase.ui.database.FirebaseRecyclerOptions;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

        import java.util.ArrayList;

import app.fyp.besecure.MainActivity;
import app.fyp.besecure.PhoneModel.AuthorityModel;
        import app.fyp.besecure.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthorityFragment extends Fragment {

    //    int images[]={R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};
    private ViewFlipper simpleViewFlipper;

    private ArrayList<String> mCategories = new ArrayList<>();

    Button mAddAuthorityBtn;
    int countInt, incrementalCount;
    DatabaseReference AuthorityReference;
    RecyclerView mProductRecycVw;
    String count;

    public AuthorityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authority, container, false);
        AuthorityReference = FirebaseDatabase.getInstance().getReference().child("Authority");
        getActivity().setTitle("Authority");

        mAddAuthorityBtn=view.findViewById(R.id.add_authority);
        mAddAuthorityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        mProductRecycVw = view.findViewById(R.id.recycler_vw_catagory);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mProductRecycVw.setLayoutManager(mLayoutManager);        // loop for creating ImageView's

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AuthorityModel> options = new FirebaseRecyclerOptions.Builder<AuthorityModel>()
                .setQuery(AuthorityReference, AuthorityModel.class)
                .build();

        FirebaseRecyclerAdapter<AuthorityModel, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<AuthorityModel, AuthorityFragment.ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AuthorityFragment.ProductViewHolder holder, int position, @NonNull AuthorityModel model) {


                DisplayMetrics displaymetrics = new DisplayMetrics();
                (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                //if you need three fix imageview in width

                holder.mNameTV.setText(model.getName());
                holder.mEmailTV.setText(model.getEmail());
                holder.mPhoneTV.setText(model.getPhone());



            }

            @NonNull
            @Override
            public AuthorityFragment.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.authority_dummy, viewGroup, false);
                AuthorityFragment.ProductViewHolder productViewHolder = new AuthorityFragment.ProductViewHolder(view);
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
        TextView mNameTV, mPhoneTV, mEmailTV;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.imageView);
            mNameTV = itemView.findViewById(R.id.name);
            mPhoneTV = itemView.findViewById(R.id.phone);
            mEmailTV = itemView.findViewById(R.id.email);


        }
    }


}

