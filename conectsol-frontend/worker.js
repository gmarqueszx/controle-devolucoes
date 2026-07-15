const BACKEND_URL = "https://conectsol-backend-qhbv3d7eya-uc.a.run.app";

export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    if (url.pathname.startsWith("/api/")) {
      const target = new URL(url.pathname + url.search, BACKEND_URL);
      return fetch(new Request(target, request));
    }

    return env.ASSETS.fetch(request);
  },
};
