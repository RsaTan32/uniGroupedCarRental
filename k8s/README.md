# k3s / Argo CD deployment

## Layout

- `base/`: Deployment, ClusterIP Service, ConfigMap, and Traefik Ingress.
- `overlays/prod/`: production namespace and GHCR image reference.
- `../argocd/application.yaml`: Argo CD Application pointing to this overlay.

## Before syncing

1. Build and publish `ghcr.io/rsatan32/unigroupedcarrental:main`, or change the
   image name and tag in `overlays/prod/kustomization.yaml` to the image you publish.
   For subsequent releases, prefer immutable version or commit-SHA tags and update
   `newTag` in Git so Argo CD detects the change.
2. Replace `grouped.example.com` in `base/ingress.yaml` with a DNS name that points
   to the VPS. The Ingress assumes the k3s Traefik ingress controller is enabled.
3. If the GHCR package is private, configure an image pull secret in the
   `car-rental` namespace; do not commit registry credentials to this repository.
4. Change the backend URLs in `base/configmap.yaml` if the upstream addresses change.

## Build the image from the repository root (PowerShell)

```powershell
.\mvnw.cmd package -DskipTests
docker build -f src/main/docker/Dockerfile.jvm -t ghcr.io/rsatan32/unigroupedcarrental:main .
docker push ghcr.io/rsatan32/unigroupedcarrental:main
```

Authenticate to `ghcr.io` with Docker before pushing. Keep the token out of command
history and repository files.

## Install the Argo CD Application

```powershell
kubectl apply -f argocd/application.yaml
```

Argo CD then syncs `k8s/overlays/prod` from the `main` branch. To validate the
rendered manifests locally, run:

```powershell
kubectl kustomize k8s/overlays/prod
```