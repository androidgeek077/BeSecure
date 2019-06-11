package app.fyp.besecure.PhoneModel;

public class ImageUploadModel {

    public ImageUploadModel(String imageurl, String userId) {
        this.imageurl = imageurl;
        this.userId = userId;
    }

    String imageurl, userId;

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public ImageUploadModel() {
    }


}
