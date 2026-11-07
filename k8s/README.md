# k3s / Argo CD deployment

## Layout

- `base/`: Deployment, ClusterIP Service, ConfigMap, and Traefik Ingress.
- `overlays/prod/`: production namespace and GHCR image reference.
- `../argocd/application.yaml`: Argo CD Application pointing to this overlay.

## Before syncing

1. The GitHub Actions workflow at `.github/workflows/build-and-deploy.yml` runs on
   pushes to `main`. It tests and packages the app, publishes a commit-SHA image to
   GHCR, and commits that SHA to `overlays/prod/kustomization.yaml`. Argo CD then
   detects the Git change and deploys the new image.
2. The workflow needs `contents: write` and `packages: write` permission. These are
   declared in the workflow; repository Actions settings must allow the workflow to
   create commits with `GITHUB_TOKEN`.
3. If the GHCR package is private, configure an image pull secret in the
   `car-rental` namespace; do not commit registry credentials to this repository.
   Alternatively, set the package visibility to public after the first image push.
4. The current Ingress host is `api.pistidevops.dev`, which is also the configured
   car-rental upstream host. Avoid routing both services through the same host and
   `/` path: use a dedicated DNS name (for example `grouped.api.pistidevops.dev`)
   pointing to the VPS, then set it in `base/ingress.yaml`. Keep the upstream URL in
   `base/configmap.yaml` pointed at the car-rental service.
5. The Ingress assumes the k3s Traefik controller is enabled. Configure DNS to point
   the dedicated Ingress hostname to the VPS public IP.

## Manual image build (optional, PowerShell)

```powershell
.\mvnw.cmd package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t ghcr.io/rsatan32/unigroupedcarrental:dev .
docker push ghcr.io/rsatan32/unigroupedcarrental:dev
```

Authenticate to `ghcr.io` with Docker before pushing. Keep the token out of command
history and repository files. A manual image tag must also be set in
`overlays/prod/kustomization.yaml` for Argo CD to deploy it.

## Install the Argo CD Application

```powershell
kubectl apply -f argocd/application.yaml
```

Argo CD then syncs `k8s/overlays/prod` from the `main` branch. To validate the
rendered manifests locally, run:

```powershell
kubectl kustomize k8s/overlays/prod
```
