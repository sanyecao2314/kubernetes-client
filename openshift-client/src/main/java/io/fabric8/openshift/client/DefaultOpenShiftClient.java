/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.openshift.client;

import com.squareup.okhttp.OkHttpClient;
import io.fabric8.kubernetes.api.model.DoneableEndpoints;
import io.fabric8.kubernetes.api.model.DoneableEvent;
import io.fabric8.kubernetes.api.model.DoneableNamespace;
import io.fabric8.kubernetes.api.model.DoneableNode;
import io.fabric8.kubernetes.api.model.DoneablePersistentVolume;
import io.fabric8.kubernetes.api.model.DoneablePersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.DoneableReplicationController;
import io.fabric8.kubernetes.api.model.DoneableResourceQuota;
import io.fabric8.kubernetes.api.model.DoneableSecret;
import io.fabric8.kubernetes.api.model.DoneableSecurityContextConstraints;
import io.fabric8.kubernetes.api.model.DoneableService;
import io.fabric8.kubernetes.api.model.DoneableServiceAccount;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.api.model.EndpointsList;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.EventList;
import io.fabric8.kubernetes.api.model.KubernetesList;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerList;
import io.fabric8.kubernetes.api.model.ResourceQuota;
import io.fabric8.kubernetes.api.model.ResourceQuotaList;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.api.model.SecurityContextConstraints;
import io.fabric8.kubernetes.api.model.SecurityContextConstraintsList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccountList;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.BaseClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.ClientKubernetesListMixedOperation;
import io.fabric8.kubernetes.client.dsl.ClientLoggableResource;
import io.fabric8.kubernetes.client.dsl.ClientMixedOperation;
import io.fabric8.kubernetes.client.dsl.ClientNonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.ClientResource;
import io.fabric8.kubernetes.client.dsl.ClientRollableScallableResource;
import io.fabric8.kubernetes.client.dsl.internal.EndpointsOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.EventOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.KubernetesListOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.NamespaceOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.NodeOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.PersistentVolumeClaimOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.PersistentVolumeOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.PodOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.ReplicationControllerOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.ResourceQuotaOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.SecretOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.SecurityContextConstraintsOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.ServiceAccountOperationsImpl;
import io.fabric8.kubernetes.client.dsl.internal.ServiceOperationsImpl;
import io.fabric8.openshift.api.model.Build;
import io.fabric8.openshift.api.model.BuildConfig;
import io.fabric8.openshift.api.model.BuildConfigList;
import io.fabric8.openshift.api.model.BuildList;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigList;
import io.fabric8.openshift.api.model.DoneableBuild;
import io.fabric8.openshift.api.model.DoneableBuildConfig;
import io.fabric8.openshift.api.model.DoneableDeploymentConfig;
import io.fabric8.openshift.api.model.DoneableGroup;
import io.fabric8.openshift.api.model.DoneableImageStream;
import io.fabric8.openshift.api.model.DoneableOAuthAccessToken;
import io.fabric8.openshift.api.model.DoneableOAuthAuthorizeToken;
import io.fabric8.openshift.api.model.DoneableOAuthClient;
import io.fabric8.openshift.api.model.DoneablePolicy;
import io.fabric8.openshift.api.model.DoneablePolicyBinding;
import io.fabric8.openshift.api.model.DoneableProject;
import io.fabric8.openshift.api.model.DoneableRoleBinding;
import io.fabric8.openshift.api.model.DoneableRoute;
import io.fabric8.openshift.api.model.DoneableTemplate;
import io.fabric8.openshift.api.model.DoneableUser;
import io.fabric8.openshift.api.model.Group;
import io.fabric8.openshift.api.model.GroupList;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.openshift.api.model.ImageStreamList;
import io.fabric8.openshift.api.model.OAuthAccessToken;
import io.fabric8.openshift.api.model.OAuthAccessTokenList;
import io.fabric8.openshift.api.model.OAuthAuthorizeToken;
import io.fabric8.openshift.api.model.OAuthAuthorizeTokenList;
import io.fabric8.openshift.api.model.OAuthClient;
import io.fabric8.openshift.api.model.OAuthClientList;
import io.fabric8.openshift.api.model.Policy;
import io.fabric8.openshift.api.model.PolicyBinding;
import io.fabric8.openshift.api.model.PolicyBindingList;
import io.fabric8.openshift.api.model.PolicyList;
import io.fabric8.openshift.api.model.Project;
import io.fabric8.openshift.api.model.ProjectList;
import io.fabric8.openshift.api.model.RoleBinding;
import io.fabric8.openshift.api.model.RoleBindingList;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteList;
import io.fabric8.openshift.api.model.Template;
import io.fabric8.openshift.api.model.TemplateList;
import io.fabric8.openshift.api.model.User;
import io.fabric8.openshift.api.model.UserList;
import io.fabric8.openshift.client.dsl.ClientBuildConfigResource;
import io.fabric8.openshift.client.dsl.ClientProjectRequestOperation;
import io.fabric8.openshift.client.dsl.ClientSubjectAccessReviewOperation;
import io.fabric8.openshift.client.dsl.ClientTemplateResource;
import io.fabric8.openshift.client.dsl.CreateableLocalSubjectAccessReview;
import io.fabric8.openshift.client.dsl.CreateableSubjectAccessReview;
import io.fabric8.openshift.client.dsl.internal.BuildConfigOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.BuildOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ClientSubjectAccessReviewOperationImpl;
import io.fabric8.openshift.client.dsl.internal.DeploymentConfigOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.GroupOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ImageStreamOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.OAuthAccessTokenOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.OAuthAuthorizeTokenOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.OAuthClientOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.PolicyBindingOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.PolicyOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ProjectOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.ProjectRequestsOperationImpl;
import io.fabric8.openshift.client.dsl.internal.RoleBindingOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.RouteOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.TemplateOperationsImpl;
import io.fabric8.openshift.client.dsl.internal.UserOperationsImpl;

import java.net.MalformedURLException;
import java.net.URL;

public class DefaultOpenShiftClient extends BaseClient implements OpenShiftClient {

  private URL openShiftUrl;

  public DefaultOpenShiftClient() throws KubernetesClientException {
    this(new OpenShiftConfigBuilder().build());
  }

  public DefaultOpenShiftClient(final Config config) throws KubernetesClientException {
    this(new OpenShiftConfig(config));
  }

  public DefaultOpenShiftClient(final OpenShiftConfig config) throws KubernetesClientException {
    super(config);
    try {
      this.openShiftUrl = new URL(config.getOpenShiftUrl());
    } catch (MalformedURLException e) {
      throw new KubernetesClientException("Could not create client", e);
    }
  }

  public DefaultOpenShiftClient(String masterUrl) throws KubernetesClientException {
    this(new OpenShiftConfigBuilder().withMasterUrl(masterUrl).build());
  }

  public DefaultOpenShiftClient(OkHttpClient httpClient, OpenShiftConfig config) throws KubernetesClientException {
    super(httpClient, config);
    try {
      this.openShiftUrl = new URL(config.getOpenShiftUrl());
    } catch (MalformedURLException e) {
      throw new KubernetesClientException("Could not create client", e);
    }
  }

  @Override
  public URL getOpenshiftUrl() {
    return openShiftUrl;
  }

  @Override
  public ClientMixedOperation<Endpoints, EndpointsList, DoneableEndpoints, ClientResource<Endpoints, DoneableEndpoints>> endpoints() {
    return new EndpointsOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientMixedOperation<Event, EventList, DoneableEvent, ClientResource<Event, DoneableEvent>> events() {
    return new EventOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientNonNamespaceOperation<Namespace, NamespaceList, DoneableNamespace, ClientResource<Namespace, DoneableNamespace>> namespaces() {
    return new NamespaceOperationsImpl(getHttpClient(), getConfiguration());
  }

  @Override
  public ClientNonNamespaceOperation<Node, NodeList, DoneableNode, ClientResource<Node, DoneableNode>> nodes() {
    return new NodeOperationsImpl(getHttpClient(), getConfiguration());
  }

  @Override
  public ClientMixedOperation<PersistentVolume, PersistentVolumeList, DoneablePersistentVolume, ClientResource<PersistentVolume, DoneablePersistentVolume>> persistentVolumes() {
    return new PersistentVolumeOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientMixedOperation<PersistentVolumeClaim, PersistentVolumeClaimList, DoneablePersistentVolumeClaim, ClientResource<PersistentVolumeClaim, DoneablePersistentVolumeClaim>> persistentVolumeClaims() {
    return new PersistentVolumeClaimOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientMixedOperation<Pod, PodList, DoneablePod, ClientLoggableResource<Pod, DoneablePod>> pods() {
    return new PodOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientMixedOperation<ReplicationController, ReplicationControllerList, DoneableReplicationController, ClientRollableScallableResource<ReplicationController, DoneableReplicationController>> replicationControllers() {
    return new ReplicationControllerOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientMixedOperation<ResourceQuota, ResourceQuotaList, DoneableResourceQuota, ClientResource<ResourceQuota, DoneableResourceQuota>> resourceQuotas() {
    return new ResourceQuotaOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientMixedOperation<Secret, SecretList, DoneableSecret, ClientResource<Secret, DoneableSecret>> secrets() {
    return new SecretOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientMixedOperation<Service, ServiceList, DoneableService, ClientResource<Service, DoneableService>> services() {
    return new ServiceOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientMixedOperation<ServiceAccount, ServiceAccountList, DoneableServiceAccount, ClientResource<ServiceAccount, DoneableServiceAccount>> serviceAccounts() {
    return new ServiceAccountOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientKubernetesListMixedOperation lists() {
    return new KubernetesListOperationsImpl(getHttpClient(), getConfiguration(), getNamespace());
  }

  @Override
  public ClientNonNamespaceOperation<SecurityContextConstraints, SecurityContextConstraintsList, DoneableSecurityContextConstraints, ClientResource<SecurityContextConstraints, DoneableSecurityContextConstraints>> securityContextConstraints() {
    return new SecurityContextConstraintsOperationsImpl(getHttpClient(), getConfiguration());
  }

  @Override
  public ClientMixedOperation<Build, BuildList, DoneableBuild, ClientResource<Build, DoneableBuild>> builds() {
    return new BuildOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientMixedOperation<BuildConfig, BuildConfigList, DoneableBuildConfig, ClientBuildConfigResource<BuildConfig, DoneableBuildConfig, Void, Void>> buildConfigs() {
    return new BuildConfigOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientMixedOperation<DeploymentConfig, DeploymentConfigList, DoneableDeploymentConfig, ClientResource<DeploymentConfig, DoneableDeploymentConfig>> deploymentConfigs() {
    return new DeploymentConfigOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientMixedOperation<Group, GroupList, DoneableGroup, ClientResource<Group, DoneableGroup>> groups() {
    return new GroupOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientMixedOperation<ImageStream, ImageStreamList, DoneableImageStream, ClientResource<ImageStream, DoneableImageStream>> imageStreams() {
    return new ImageStreamOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientNonNamespaceOperation<OAuthAccessToken, OAuthAccessTokenList, DoneableOAuthAccessToken, ClientResource<OAuthAccessToken, DoneableOAuthAccessToken>> oAuthAccessTokens() {
    return new OAuthAccessTokenOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public ClientNonNamespaceOperation<OAuthAuthorizeToken, OAuthAuthorizeTokenList, DoneableOAuthAuthorizeToken, ClientResource<OAuthAuthorizeToken, DoneableOAuthAuthorizeToken>> oAuthAuthorizeTokens() {
    return new OAuthAuthorizeTokenOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public ClientNonNamespaceOperation<OAuthClient, OAuthClientList, DoneableOAuthClient, ClientResource<OAuthClient, DoneableOAuthClient>> oAuthClients() {
    return new OAuthClientOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public ClientMixedOperation<Policy, PolicyList, DoneablePolicy, ClientResource<Policy, DoneablePolicy>> policies() {
    return new PolicyOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientMixedOperation<PolicyBinding, PolicyBindingList, DoneablePolicyBinding, ClientResource<PolicyBinding, DoneablePolicyBinding>> policyBindings() {
    return new PolicyBindingOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientNonNamespaceOperation<Project, ProjectList, DoneableProject, ClientResource<Project, DoneableProject>> projects() {
    return new ProjectOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public ClientProjectRequestOperation projectrequests() {
    return new ProjectRequestsOperationImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()));
  }

  @Override
  public ClientMixedOperation<RoleBinding, RoleBindingList, DoneableRoleBinding, ClientResource<RoleBinding, DoneableRoleBinding>> roleBindings() {
    return new RoleBindingOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientMixedOperation<Route, RouteList, DoneableRoute, ClientResource<Route, DoneableRoute>> routes() {
    return new RouteOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientMixedOperation<Template, TemplateList, DoneableTemplate, ClientTemplateResource<Template, KubernetesList, DoneableTemplate>> templates() {
    return new TemplateOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientMixedOperation<User, UserList, DoneableUser, ClientResource<User, DoneableUser>> users() {
    return new UserOperationsImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public ClientSubjectAccessReviewOperation<CreateableSubjectAccessReview, CreateableLocalSubjectAccessReview> subjectAccessReviews() {
    return new ClientSubjectAccessReviewOperationImpl(getHttpClient(), OpenShiftConfig.wrap(getConfiguration()), getNamespace());
  }

  @Override
  public OpenShiftClient inNamespace(String namespace) {
    OpenShiftConfig updated = new OpenShiftConfigBuilder(new OpenShiftConfig(getConfiguration()))
      .withOpenShiftUrl(openShiftUrl.toString())
      .withNamespace(namespace)
      .build();
    return new DefaultOpenShiftClient(getHttpClient(), updated);
  }

  @Override
  public OpenShiftClient inAnyNamespace() {
    return inNamespace(null);
  }
}
