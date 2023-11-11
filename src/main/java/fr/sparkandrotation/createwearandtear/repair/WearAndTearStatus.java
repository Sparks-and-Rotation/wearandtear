package fr.sparkandrotation.createwearandtear.repair;

public enum WearAndTearStatus {

    OK(false),
    DESTROY(true);
    private final boolean isDestroy;

    WearAndTearStatus(boolean isDestroy) {
        this.isDestroy =isDestroy;
    }

    public boolean isDestroy() {
        return this.isDestroy;
    }
}
