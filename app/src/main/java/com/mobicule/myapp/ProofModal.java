package com.mobicule.myapp;
public class ProofModal {
    String proofName;
    String imageSrc;

    public ProofModal() {
    }

    public ProofModal(String proofName, String imageSrc) {
        this.proofName = proofName;
        this.imageSrc = imageSrc;
    }

    public String getProofName() {
        return proofName;
    }

    public void setProofName(String proofName) {
        this.proofName = proofName;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }
}